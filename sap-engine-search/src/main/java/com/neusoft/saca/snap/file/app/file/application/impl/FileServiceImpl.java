package com.neusoft.saca.snap.file.app.file.application.impl;

import static org.springframework.data.mongodb.core.query.Query.query;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import net.sf.json.JSONObject;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.Splitter;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.file.application.FileService;
import com.neusoft.saca.snap.file.app.file.constant.FileConstant;
import com.neusoft.saca.snap.file.app.image.application.ImageService;
import com.neusoft.saca.snap.file.app.image.application.ImageStoreService;
import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;
import com.neusoft.saca.snap.file.domain.UploadReq;
import com.neusoft.saca.snap.file.domain.ticket.api.TicketManageFacade;
import com.neusoft.saca.snap.file.infrastructure.converter.PdfConvertService;
import com.neusoft.saca.snap.file.infrastructure.util.FileTypeUtil;
import com.neusoft.saca.snap.file.infrastructure.util.ImageScaleUtil;
import com.neusoft.saca.snap.file.infrastructure.util.VoiceChangeUtil;
import com.neusoft.saca.snap.file.infrastructure.util.ZipUtils;
import com.neusoft.saca.snap.infrastructure.spring.SpringContextUtil;
import com.neusoft.saca.snap.search.application.SearchIndexService;

@Service("FileServiceImpl")
public class FileServiceImpl implements FileService {
	// JVM的系统临时文件夹
	private static String TMP_DIR = System.getProperties().getProperty("java.io.tmpdir");
	static {
		TMP_DIR = TMP_DIR.replaceAll("\\\\", "/");
		if (TMP_DIR.lastIndexOf("/") != (TMP_DIR.length() - 1)) {
			TMP_DIR = TMP_DIR + "/";
		}
	}

	@SuppressWarnings("serial")
	private static final List<String> IMAGE_TYPE = new ArrayList<String>() {
		{
			add("JPEG");//
			add("JPG");//
			add("GIF");//
			add("PNG");//
			add("BMP");//
		}
	};

	// 默认显示的pdfId，用来显示无法预览的文件
	private static String DEFAULT_PDF_ID = "a78ba870-9327-4ef8-adc8-b639f7596433";

	private static final String JPG_CONTENT_TYPE = "image/jpeg";
	private static final String JPG_SUFFIX = "jpg";

	@Autowired
	private PdfConvertService pdfConvertService;

	@Autowired
	@Qualifier("sourceGridfs")
	private GridFsOperations srcGridfsOperations;

	@Autowired
	@Qualifier("pdfGridfs")
	private GridFsOperations pdfGridfsOperations;

	@Autowired
	@Qualifier("imageGridfs")
	private GridFsOperations imageGridFsOperations;

	@Autowired
	@Qualifier("pdfTaskExecutor")
	private ThreadPoolTaskExecutor pdfTaskExecutor;

	@Autowired
	private SearchIndexService searchIndexService;

	@Autowired
	private TicketManageFacade ticketManageFacade;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ImageStoreService imageStoreService;

	
	private void changeSrcFilePreviewStatus(String fileId,String status){
		GridFSDBFile srcFile = srcGridfsOperations.findOne(buildFindByIdQuery(fileId));
		if (srcFile != null) {
			DBObject metaData = srcFile.getMetaData();
			metaData.put("previewStatus", status);
			srcFile.save();
		}
	}
	
	@Override
	public boolean upload(final String id, final UploadReq uploadReq, final MultipartFile mfile) throws IOException {
		// 文件的原始名称
		final String originalFilename = uploadReq.getOriginalFilename();
		// uploadReq.setOriginalFilename(originalFilename);
		// 文件类型
		String fileType = FilenameUtils.getExtension(originalFilename);
		// 存储文件名
		String storedFilename = id + "." + fileType;
		
		String contentType = mfile.getContentType();
		// 指定文件id
		// uploadReq.setFid(id);

		// 获取原始流文件
		InputStream is = mfile.getInputStream();
		// 这里可按需要进行压缩处理后再存储
		if (uploadReq.isNeedCompress()) {
			byte[] bytes = mfile.getBytes();
			byte[] zippedByteArray = ZipUtils.zipByteArray(bytes, originalFilename);
			is = new ByteArrayInputStream(zippedByteArray);
			storedFilename = id + ".zip";
		}
		
		// 在指定的GridFS Bucket中存储文件
		restoreFile(srcGridfsOperations, storedFilename, contentType, uploadReq, is);

		// 视情况进行pdf文件转换
		if (!uploadReq.isNeedPdfPreview()) {
			mfile.getInputStream().close();
			return true;
		}

		// 根据字节码判断文件类型
		byte[] header = new byte[50];
		
		mfile.getInputStream().read(header);
		final String fileRealType = FileTypeUtil.getFileType(header, fileType);

		// 执行pdf生成，并存储到对应的库
		Runnable runnable = null;
		
		// 类型为pdf，直接复存
		if ("pdf".equalsIgnoreCase(fileRealType)) {
			runnable = new Runnable() {
				@Override
				public void run() {
					try {
						InputStream inputStream = mfile.getInputStream();
						// 保存pdf
						uploadReq.setPreviewStatus(FileConstant.FILE_PREVIEW_STATUS_PREVIEWABLE);
						restoreFile(pdfGridfsOperations, id + ".pdf", "application/pdf", uploadReq, inputStream);
						try {
							// 保存pdf缩略图
							saveThumbnail(mfile.getInputStream(), id, uploadReq);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						//分割pdf并保存
						splitAndSavePdf(id + ".pdf", "application/pdf", uploadReq, mfile.getInputStream());
						
						//更新src文件的预览状态
						changeSrcFilePreviewStatus(id, FileConstant.FILE_PREVIEW_STATUS_PREVIEWABLE);
						/**
						 * 判断redis中是否有fileId,如果有，说明业务服务器已经建立完索引，可以进行文本索引建立
						 * 如果没有，说明业务没有结束，此时不能进行文本索引建立，同时将fileId ticket写入redis，
						 * 使业务完成后能够获取到，并主动通知文件服务器进行内容索引建立
						 */
						if (ticketManageFacade.obtainValue(id, "search") != null) {
							// 为pdf文本建索引
							searchIndexService.indexFileContent(id);
						} else {
							ticketManageFacade.createTicket(id, id, "search");
						}
						mfile.getInputStream().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			pdfTaskExecutor.execute(runnable);
		} else if (pdfConvertService.isSupportByExtension(fileRealType)
				&& pdfConvertService.isSupportByExtension(fileType)) {
			//根据扩展名判断是否支持此种格式的转换
			runnable = new Runnable() {
				@Override
				public void run() {
					FileService fileServiceImpl = (FileService) SpringContextUtil.getBean("FileServiceImpl");
					try {
						fileServiceImpl.convertToPdfAndRestore(uploadReq, mfile, fileRealType);
						/**
						 * 判断redis中是否有fileId,如果有，说明业务服务器已经建立完索引，可以进行文本索引建立
						 * 如果没有，说明业务没有结束，此时不能进行文本索引建立，同时将fileId ticket写入redis，
						 * 使业务完成后能够获取到，并主动通知文件服务器进行内容索引建立
						 */
						if (ticketManageFacade.obtainValue(id, "search") != null) {
							// 为pdf文本建索引
							searchIndexService.indexFileContent(id);
						} else {
							ticketManageFacade.createTicket(id, id, "search");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			pdfTaskExecutor.execute(runnable);
		} else if (IMAGE_TYPE.contains(fileType.toUpperCase())) {
			//文件类型为图片类型
			runnable = new Runnable() {
				@Override
				public void run() {
					String name = mfile.getOriginalFilename();
					String imageType = name.substring(name.lastIndexOf(".") + 1, name.length());

					File originalImageFile = new File(TMP_DIR + id + "." + imageType);
					if (!originalImageFile.exists()) {
						originalImageFile.mkdirs();
					}

					try {

						// 保存图片到临时文件夹
						mfile.transferTo(originalImageFile);
						if (IMAGE_TYPE.contains(imageService.getImageType(originalImageFile).toUpperCase())) {
							List<Double> thumbnails = SnapFileConfigUtils.obtainImageScaleSizes();
							// 保存原始图片
							InputStream originalInputStream = new FileInputStream(originalImageFile);
							JSONObject metaData=new JSONObject();
							metaData.put("attachFlag", false);
							metaData.put("fid", id);
							metaData.put("previewStatus", FileConstant.FILE_PREVIEW_STATUS_PREVIEWABLE);//设置可预览状态
							changeSrcFilePreviewStatus(id, FileConstant.FILE_PREVIEW_STATUS_PREVIEWABLE);
							imageStoreService.save(id, originalInputStream,metaData);
							originalInputStream.close();
							
							Map<Double, InputStream> rectPictureMap = ImageScaleUtil.rectPicture(originalImageFile,
									thumbnails);

							for (Double thumbnail : rectPictureMap.keySet()) {
								// 保存缩略图
								String thumbnailImageId = id + "_" + thumbnail.intValue();
								InputStream inputStream = rectPictureMap.get(thumbnail);
								// unattachedMetaData 需要修正
								imageStoreService.save(thumbnailImageId, inputStream, metaData);

								inputStream.close();
							}
						}
						originalImageFile.delete(); // 删除临时图片
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			pdfTaskExecutor.execute(runnable);
		}
		return true;
	}

	private void saveThumbnail(InputStream inputStream, String id, UploadReq uploadReq) {
		Document document = null;
		try {
			float rotation = 0f;
			// 缩略图显示倍数，1表示不缩放，0.5表示缩小到50%
			float zoom = 0.8f;

			document = new Document();
			document.setInputStream(inputStream, null);
			// document.setFile(inputFile);
			// maxPages = document.getPageTree().getNumberOfPages();

			BufferedImage image = (BufferedImage) document.getPageImage(0, GraphicsRenderingHints.SCREEN,
					Page.BOUNDARY_CROPBOX, rotation, zoom);

			Iterator iter = ImageIO.getImageWritersBySuffix(JPG_SUFFIX);
			ImageWriter writer = (ImageWriter) iter.next();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageOutputStream outImage = ImageIO.createImageOutputStream(outputStream);

			writer.setOutput(outImage);
			writer.write(new IIOImage(image, null, null));

			InputStream storedInputStream = new ByteArrayInputStream(outputStream.toByteArray());

			restoreFile(imageGridFsOperations, id, JPG_CONTENT_TYPE, uploadReq, storedInputStream);

		} catch (Exception e) {
			// log.warn("to generate thumbnail of a book fail : " + inputFile);
			// log.warn(e);
			e.printStackTrace();
		}
	}

	/**
	 * 在指定的GridFS Bucket中存储文件
	 * 
	 * @param gridFsOperations
	 * @param id
	 * @param fileName
	 * @param contentType
	 * @param metadata
	 * @param data
	 * @throws IOException
	 */
	private void restoreFile(GridFsOperations gridFsOperations, String fileName, String contentType,
			UploadReq uploadReq, InputStream data) throws IOException {

		gridFsOperations.store(data, fileName, contentType, uploadReq);
	}

	private Query buildFindByIdQuery(String id) {
			return query(GridFsCriteria.whereMetaData("fid").is(id));
	}

	private Query buildFindByIdsQuery(List<String> ids) {
		return query(GridFsCriteria.whereMetaData("fid").in(ids));
	}
	
	private Query buildFindByIdPdfQuery(String id){
		return query(GridFsCriteria.whereMetaData("fid").regex(id + "-[0-9]+"));
	}

	/**
	 * 判断文件的类型，普通office类型和txt需要进行pdf转换保存，pdf复存
	 * 
	 * @throws IOException
	 */
	public void convertToPdfAndRestore(UploadReq uploadedReq, MultipartFile mFile, String fileRealType)
			throws IOException {
		String id = uploadedReq.getFid();

		// 先把文件暂存到磁盘
		File sourceFileTmp = new File(TMP_DIR + id + "_tmp." + fileRealType);
		mFile.transferTo(sourceFileTmp);

		// 设定待生成的pdf文件
		File pdfFile = new File(TMP_DIR + id);
		pdfFile.createNewFile();
		try {
			pdfConvertService.doc2Pdf(sourceFileTmp, pdfFile);
			// 保存pdf
			uploadedReq.setPreviewStatus(FileConstant.FILE_PREVIEW_STATUS_PREVIEWABLE);
			restoreFile(pdfGridfsOperations, id + ".pdf", "application/pdf", uploadedReq, new FileInputStream(pdfFile));
			// 保存pdf缩略图
			saveThumbnail(new FileInputStream(pdfFile), id, uploadedReq);
			//更新src文件的可预览状态
			changeSrcFilePreviewStatus(id, FileConstant.FILE_PREVIEW_STATUS_PREVIEWABLE);
			
			//分割pdf并保存
			splitAndSavePdf(id + ".pdf", "application/pdf", uploadedReq, new FileInputStream(pdfFile));
			
		} finally {
			// 删除磁盘上的临时文件
			pdfFile.delete();
			sourceFileTmp.delete();

			try {
				mFile.getInputStream().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public InputStream obtainPdf(String id) {
		GridFSDBFile pdfGridFSDBFile = pdfGridfsOperations.findOne(buildFindByIdQuery(id));

		// 如果pdf文件不存在，则使用默认的那个"暂未生成预览"的pdf文件
		if (pdfGridFSDBFile == null) {
			pdfGridFSDBFile = pdfGridfsOperations.findOne(buildFindByIdQuery(DEFAULT_PDF_ID));
		}

		return pdfGridFSDBFile.getInputStream();
	}

	@Override
	public InputStream obtainPdfWithoutDefault(String fileId) {
		GridFSDBFile pdfGridFSDBFile = pdfGridfsOperations.findOne(buildFindByIdQuery(fileId));

		if (pdfGridFSDBFile != null) {
			return pdfGridFSDBFile.getInputStream();
		}
		return null;
	}

	@Override
	public GridFSDBFile obtainOriginalFile(String id) {
		return obtainFile(srcGridfsOperations, id);
	}

	private GridFSDBFile obtainFile(GridFsOperations gridfsOperations, String id) {
		List<GridFSDBFile> result = gridfsOperations.find(buildFindByIdQuery(id));
		return result != null && result.size() > 0 ? result.get(0) : null;
	}

	@Override
	public void delete(String fileId) {
		srcGridfsOperations.delete(buildFindByIdQuery(fileId));
		pdfGridfsOperations.delete(buildFindByIdQuery(fileId));
		//删除pdf的分页文件三
		pdfGridfsOperations.delete(buildFindByIdPdfQuery(fileId));
	}

	@Override
	public void delete(List<String> fileIds) {
		if (fileIds != null && fileIds.size() > 0) {
			//srcGridfsOperations.delete(buildFindByIdsQuery(fileIds));
			//pdfGridfsOperations.delete(buildFindByIdsQuery(fileIds));
			for(String fileId:fileIds){
				delete(fileId);
			}
		}
	}

	@Override
	public void attachFile(String fileId) {
		// 先更新源文件的attachFlag
		String fileType=null;
		GridFSDBFile srcFile = srcGridfsOperations.findOne(buildFindByIdQuery(fileId));
		if (srcFile != null) {
			DBObject metaData = srcFile.getMetaData();
			metaData.put("attachFlag", true);
			srcFile.save();
			Object type=metaData.get("type");
			if (type!=null) {
				fileType=(String)type;
			}
		}
		
		if (IMAGE_TYPE.contains(fileType)) {//如果是图片，更新图片库中的预览文件
			GridFSDBFile imageFile = imageGridFsOperations.findOne(buildFindByIdQuery(fileId));
			if (imageFile!=null) {
				DBObject metaData = imageFile.getMetaData();
				metaData.put("attachFlag", true);
				imageFile.save();
			}
			List<Double> thumbnails = SnapFileConfigUtils.obtainImageScaleSizes();
			for (Double thumbnail : thumbnails) {//设置切割图片的标志
				GridFSDBFile thumbnailFile = imageGridFsOperations.findOne(buildFindByIdQuery(fileId+"_"+thumbnail.intValue()));
				if (thumbnailFile!=null) {
					DBObject metaData = thumbnailFile.getMetaData();
					metaData.put("attachFlag", true);
					thumbnailFile.save();
				}
			}
		}else{
			// 更新预览的pdf文件的attachFlag
			GridFSDBFile pdfFile = pdfGridfsOperations.findOne(buildFindByIdQuery(fileId));
			if (pdfFile != null) {
				DBObject metaData = pdfFile.getMetaData();
				metaData.put("attachFlag", true);
				pdfFile.save();
			}			
			//更新分页pdf文件的acctachFlag
			int i = 0;
			GridFSDBFile splitFile = null;
			while((splitFile = pdfGridfsOperations.findOne(buildFindByIdQuery(fileId + "-" + i))) != null){
				i++;
				DBObject metaData = splitFile.getMetaData();
				metaData.put("attachFlag", true);
				splitFile.save();
			}
		}
	}

	@Override
	public void batchAttachFile(List<String> fileIds) {
		// TODO 批量操作，不用循环
		if (fileIds != null && fileIds.size() > 0) {
			for (String fileId : fileIds) {
				attachFile(fileId);
			}
		}
	}

	@Override
	public String obtainFilePreviewStatus(String fileId) {
		GridFSDBFile srcFile = srcGridfsOperations.findOne(buildFindByIdQuery(fileId));
		if (srcFile != null) {
			DBObject metaData = srcFile.getMetaData();
			String status=(String)metaData.get("previewStatus");
			if (StringUtils.isNotBlank(status)) {
				return status;
			}
		}
		return FileConstant.FILE_PREVIEW_STATUS_UNPREVIEWABLE;
	}

	@Override
	public boolean uploadVoice(final String id,final UploadReq uploadedReq,final  MultipartFile file) throws IOException {
		final String originalFilename = uploadedReq.getOriginalFilename();// 文件的原始名称

		final String fileType = FilenameUtils.getExtension(originalFilename);
		String storedFilename = id + "." + fileType;

		String contentType = file.getContentType();

		// 保存原始文件
		InputStream is = file.getInputStream();
		restoreFile(srcGridfsOperations, storedFilename, contentType, uploadedReq, is);
		//关闭MultipartFile的文件输入流
		file.getInputStream().close();
		
		// 执行amr转mp3
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String mp3Id=id+"_mp3";
				UploadReq uploadReq4Mp3=new UploadReq();
				uploadReq4Mp3.setFid(mp3Id);
				uploadReq4Mp3.setCreator(uploadedReq.getCreator());
				uploadReq4Mp3.setOriginalFilename(mp3Id+".mp3");
				uploadReq4Mp3.setType("mp3");
				
				File tempFile=new File(TMP_DIR + id + "_tmp." + fileType);
				try {
					file.transferTo(tempFile);
					File targetFile=new File(TMP_DIR + mp3Id + ".mp3");
					VoiceChangeUtil.amr2mp3(tempFile, targetFile);
					InputStream inputStream=new FileInputStream(targetFile);
					restoreFile(srcGridfsOperations, mp3Id+".mp3", "audio/mp3", uploadReq4Mp3, inputStream);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					tempFile.delete();
				}
			}
		};

		pdfTaskExecutor.execute(runnable);//借用pdf转化的线程池
		
		return true;
	}

	/**
	 * 将pdf文件按页分成多个文件，并分别保存到mongodb中
	 * @author libaoyu 2016-4-20
	 * @param gridFsOperations
	 * @param fileName
	 * @param contentType
	 * @param uploadReq
	 * @param data
	 */
	private void splitAndSavePdf(String fileName, String contentType,
			UploadReq uploadReq, InputStream data){
		PDDocument document = null;
		List<PDDocument> documents = null;
		Splitter splitter = new Splitter();
		//设置每个文件1页
		splitter.setSplitAtPage(1);
		try
        {
			document = PDDocument.load(data);
			documents = splitter.split( document );
			//保存原文件的总页数到每一个文件中
			uploadReq.setPages(documents.size());
            for( int i=0; i<documents.size(); i++ )
            {
                PDDocument doc = documents.get( i );
                String fn = fileName.substring(0, fileName.length()-4 ) + "-" + i + ".pdf";
                uploadReq.setFid(fileName.substring(0, fileName.length()-4 ) + "-" + i);
                try {
					writeDocument( doc, TMP_DIR +  fn );
					//保存到mongdb
					restoreFile(pdfGridfsOperations, fn, "application/pdf", uploadReq, new FileInputStream(TMP_DIR +  fn));
				} catch (COSVisitorException e) {
					e.printStackTrace();
				}finally{
					doc.close();
					//删除临时文件
					(new File(fn)).delete();
				}
            }
        } catch (IOException e) {
			e.printStackTrace();
		}finally
        {
            if( document != null )
            {
                try {
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            for( int i=0; documents != null && i<documents.size(); i++ )
            {
                PDDocument doc = (PDDocument)documents.get( i );
                try {
					doc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
            
        }
		
	}
	
	private  void writeDocument( PDDocument doc, String fileName ) throws IOException, COSVisitorException
    {
        FileOutputStream output = null;
        COSWriter writer = null;
        try
        {
            output = new FileOutputStream( fileName );
            writer = new COSWriter( output );
            writer.write( doc );
        }
        finally
        {
            if( output != null )
            {
                output.close();
            }
            if( writer != null )
            {
                writer.close();
            }
        }
    }

	@Override
	public JSONObject obtainPdfPages(String fileId) {
		JSONObject jsonObject = new JSONObject();
		//分页存储的pdf文件，第一页是fid是fileId + "-0"
		GridFSDBFile pdfFile = pdfGridfsOperations.findOne(buildFindByIdQuery(fileId + "-0"));
		if (pdfFile != null) {
			DBObject metaData = pdfFile.getMetaData();
			int pages = (int) metaData.get("pages");
			jsonObject.put("code", 0);
			jsonObject.put("msg", "success");
			jsonObject.put("pages", pages);
			return jsonObject;
			
		}
		jsonObject.put("code", -1);
		jsonObject.put("msg", "预览文件不存在");
		return jsonObject;
	}

}
