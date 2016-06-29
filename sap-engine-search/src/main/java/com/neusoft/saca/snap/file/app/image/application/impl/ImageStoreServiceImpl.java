package com.neusoft.saca.snap.file.app.image.application.impl;

import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.neusoft.saca.snap.file.app.image.application.ImageStoreService;
import com.neusoft.saca.snap.file.constant.SnapFileConfigUtils;

@Service
public class ImageStoreServiceImpl implements ImageStoreService {

	@Autowired
	@Qualifier("imageGridfs")
	private GridFsOperations operations;

	@Override
	public void save(String id, InputStream inputstream, Object metaData) {
		operations.delete(query(whereFilename().is(id)));
		operations.store(inputstream, id, metaData);
	}

	@Override
	public String save(InputStream inputstream, Object metaData) {
		String id = UUID.randomUUID().toString();
		this.save(id, inputstream, metaData);
		return id;
	}

	@Override
	public GridFSDBFile get(String id) {
		return operations.findOne(query(whereFilename().is(id)));
	}

	@Override
	public void delete(String id) {
		//libaoyu modified，20160428，删除图片文件以及缩略图
		//operations.delete(query(whereFilename().is(id)));
		//避免错误删除，比如删除时输入一个字母，则所有包含该字母的记录都将被删除
		if(id.length() >= 36){
			operations.delete(query(whereFilename().regex(id)));
		}
		
		/*
		List<Double> thumbnails = SnapFileConfigUtils.obtainImageScaleSizes();
		for (Double thumbnail : thumbnails) {
			operations.delete(query(whereFilename().is(id + "_" + thumbnail)));
		}*/
	}

	@Override
	public void deleteByIdList(List<String> fileIdList) {
		if (fileIdList != null && fileIdList.size() > 0) {
			operations.delete(query(whereFilename().in(fileIdList)));
			List<Double> thumbnailsDouble = SnapFileConfigUtils.obtainImageScaleSizes();
			List<String> thumbnails = new ArrayList<String>();
			for (String fileId : fileIdList) {
				for (Double thumbnailDouble : thumbnailsDouble) {
					thumbnails.add(fileId + "_" + thumbnailDouble);
				}
			}
			operations.delete(query(whereFilename().in(thumbnails)));
		}
	}

	@Override
	public void attachImage(String imageId) {
		// 先更新原始图片的attachFlag
		GridFSDBFile srcImage = operations.findOne(query(whereFilename().is(imageId)));
		if (srcImage != null) {
			DBObject metaData = srcImage.getMetaData();
			metaData.put("attachFlag", true);
			srcImage.save();
		}
		// 更新图片缩略图的attachFlag
		List<Double> thumbnailsDouble = SnapFileConfigUtils.obtainImageScaleSizes();
		for (Double thumbnailDouble : thumbnailsDouble) {
			GridFSDBFile thumbnailImage = operations
					.findOne(query(whereFilename().is(imageId + "_" + thumbnailDouble)));
			if (thumbnailImage != null) {
				DBObject metaData = thumbnailImage.getMetaData();
				metaData.put("attachFlag", true);
				thumbnailImage.save();
			}
		}

	}

	@Override
	public void batchAttachImage(List<String> imageIds) {
		//TODO 批量操作，不用循环
		if (imageIds!=null&&imageIds.size()>0) {
			for (String imageId : imageIds) {
				attachImage(imageId);
			}
		}
	}

	@Override
	public List<GridFSDBFile> getList(List<String> idList) {
		if(CollectionUtils.isNotEmpty(idList)) {
			return operations.find(query(whereFilename().in(idList)));
		} else {
			return new ArrayList<GridFSDBFile>();
		}
	}

}
