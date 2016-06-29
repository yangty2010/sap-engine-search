package com.neusoft.saca.snap.file.domain.file.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neusoft.saca.snap.file.app.file.application.FileService;
import com.neusoft.saca.snap.file.app.file.constant.FileConstant;
import com.neusoft.saca.snap.file.app.file.dto.FileCreateDto;
import com.neusoft.saca.snap.file.app.file.dto.FileUpdateDto;
import com.neusoft.saca.snap.file.app.file.dto.FilesCreateDto;
import com.neusoft.saca.snap.file.domain.directory.api.DirectoryFacade;
import com.neusoft.saca.snap.file.domain.file.api.FileDomainConstant;
import com.neusoft.saca.snap.file.domain.file.repository.FileJpaRepo;
import com.neusoft.saca.snap.file.domain.file.vo.FileEntry;
import com.neusoft.saca.snap.infrastructure.constant.PrivacyConstant;
import com.neusoft.saca.snap.infrastructure.search.application.SearchableResourceIndexUpdateService;
import com.neusoft.saca.snap.infrastructure.util.BeanUtil;
import com.neusoft.saca.snap.infrastructure.util.DateUtil;
import com.neusoft.saca.snap.search.application.AppSearchableResourceIndexUpdateService;

@Service
public class FileDomainServiceImpl implements FileDomainService {

	@Autowired
	private DirectoryFacade directoryFacade;

	@Autowired
	private FileJpaRepo fileJpaRepo;

	@Autowired
	private FileService fileService;

	@Autowired
	private AppSearchableResourceIndexUpdateService appSearchableResourceIndexUpdateService;

	@Autowired
	private SearchableResourceIndexUpdateService searchableResourceIndexUpdateService;

	@Override
	@Transactional(readOnly = false)
	public Page<FileEntry> obtainFiles(int page, int size, boolean inFeed) {
		if (page < 1 || size < 1) {
			throw new IllegalArgumentException("page或者size不能小于1");
		}
		Pageable pageable = new PageRequest(page - 1, size);
		if (inFeed) {
			return fileJpaRepo.findByScopeType(
					FileDomainConstant.FILE_SCOPE_TYPE_MICROBLOG, pageable);
		} else {
			return fileJpaRepo.findByScopeTypeNot(
					FileDomainConstant.FILE_SCOPE_TYPE_MICROBLOG, pageable);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Page<FileEntry> obtainFiles(int page, int size) {
		if (page < 1 || size < 1) {
			throw new IllegalArgumentException("page或者size不能小于1");
		}
		Pageable pageable = new PageRequest(page - 1, size);
		return fileJpaRepo.findByScopeTypeNot(
				FileDomainConstant.FILE_SCOPE_TYPE_IM, pageable);
	}

	@Override
	@Transactional(readOnly = false)
	public FileEntry delete(String id) {
		FileEntry fileEntry = fileJpaRepo.findOne(id);
		if (fileEntry != null) {
			fileJpaRepo.delete(id);
			// 文件所在目录的文件计数减1
			directoryFacade.decreaseFileAmount(fileEntry.getScopeId(), 1);
			// 文件的数据流文件删除
			fileService.delete(fileEntry.getId());
			// 删除文件索引
			searchableResourceIndexUpdateService
					.deleteResourceBeanById(fileEntry.getId());
		}
		return fileEntry;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FileEntry> obtainFilesByIds(List<String> ids) {
		if (ids == null || ids.size() == 0) {
			return new ArrayList<FileEntry>();
		}
		return fileJpaRepo.findByIdList(ids);
	}

	@Override
	@Transactional(readOnly = true)
	public FileEntry obtain(String id) {
		return fileJpaRepo.findOne(id);
	}

	private void checkPageAndSize(int page, int size) {
		if (page < 1 || size < 1) {
			throw new IllegalArgumentException("输入的page或者size不正确");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FileEntry> obtainFilesByPublisher(String publisher, Sort sort,
			int page, int size) {
		checkPageAndSize(page, size);
		Pageable pageable = new PageRequest(page - 1, size, sort);
		return fileJpaRepo.findByPublisher(publisher, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FileEntry> obtainUserFilesWithPrivacy(String currentUserId,
			String publisher, List<String> orgIds, Sort sort, int page, int size) {
		if (page < 1 || size < 1) {
			throw new IllegalArgumentException("page或者size不能小于1");
		}
		Pageable pageable = new PageRequest(page - 1, size, sort);

		return fileJpaRepo.findByPublisherWithPrivacy(publisher, currentUserId,
				orgIds, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FileEntry> obtainFilesByScopeId(String scopeId, Sort sort,
			int page, int size) {
		checkPageAndSize(page, size);
		Pageable pageable = new PageRequest(page - 1, size, sort);
		return fileJpaRepo.findByScopeId(scopeId, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FileEntry> obtainFilesByScopeIdWithPrivacy(
			String currentUserId, String scopeId, List<String> orgIds,
			Sort sort, int page, int size) {
		checkPageAndSize(page, size);
		Pageable pageable = new PageRequest(page - 1, size, sort);
		return fileJpaRepo.findByScopeIdWithPrivacy(scopeId, currentUserId,
				orgIds, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FileEntry> obtainFilesByScopeId(String scopeId, Sort sort) {
		return fileJpaRepo.findByScopeId(scopeId, sort);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isFileExist(String id) {
		FileEntry fileEntry = fileJpaRepo.findOne(id);
		if (fileEntry == null) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional(readOnly = false)
	public FileEntry create(FileCreateDto fileCreateDto) {
		FileEntry fileEntry = new FileEntry();
		return fileEntry;
	}

	@Override
	@Transactional(readOnly = false)
	public FileEntry update(FileUpdateDto fileUpdateDto) {
		FileEntry fileEntry = obtain(fileUpdateDto.getId());
		if (fileEntry != null) {
			// 更新索引（tags，privacy，description）
			Map<String, Object> updateFields = new HashMap<String, Object>();
			updateFields.put("description", fileUpdateDto.getDescription());
			updateFields.put("resourcePrivacy", fileUpdateDto.getPrivacy());
			if (StringUtils.isNotBlank(fileUpdateDto.getPrivacyMember())) {
				String[] members = fileUpdateDto.getPrivacyMember().split(",");
				JSONArray jsonArray = new JSONArray();
				for (String member : members) {
					jsonArray.add(member);
				}
				updateFields.put("resourceMembers", jsonArray);
			}
			if (StringUtils.isNotBlank(fileUpdateDto.getTags())) {
				String[] tags = fileUpdateDto.getTags().split(",");
				JSONArray jsonArray = new JSONArray();
				for (String tag : tags) {
					jsonArray.add(tag);
				}
				updateFields.put("tags", jsonArray);
			}
			searchableResourceIndexUpdateService.atomSet(fileEntry.getId(),
					updateFields);
			BeanUtil.copyBeanProperties(fileEntry, fileUpdateDto);
			return fileJpaRepo.save(fileEntry);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly = false)
	public List<FileEntry> create(FilesCreateDto filesCreateDto) {
		String filesJsonStr = filesCreateDto.getFilesJson();
		JSONArray jsonArray = JSONArray.fromObject(filesJsonStr);
		List<FileEntry> files = new ArrayList<FileEntry>();
		List<String> fileIds = new ArrayList<String>();
		return files;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateFileScope(String fileId, String scopeId, String scopeName) {
		FileEntry fileEntry = fileJpaRepo.findOne(fileId);
		if (fileEntry == null) {
			throw new IllegalArgumentException("文件不存在，id: " + fileId);
		}
		String oldScopeId = fileEntry.getScopeId();

		fileEntry.setScopeId(scopeId);
		fileEntry.setScopeName(scopeName);

		fileJpaRepo.save(fileEntry);
		// 文件所在目录的文件计数加1
		directoryFacade.addFileAmount(fileEntry.getScopeId(), 1);
		// 文件所在目录的文件计数减1
		directoryFacade.decreaseFileAmount(oldScopeId, 1);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<FileEntry> obtainScopeFilesWithPrivacy(String currentUserId,
			String scopeId, Sort sort, int page, int size) {
		if (page < 1 || size < 1) {
			throw new IllegalArgumentException("page或者size不能小于1");
		}
		Pageable pageable = new PageRequest(page - 1, size, sort);

		return fileJpaRepo.findByScopeIdWithPrivacy(scopeId, currentUserId,
				pageable);
	}

	@Override
	@Transactional(readOnly = false)
	public FileEntry updatePdfPreview(String id) {
		FileEntry fileEntry = obtain(id);
		if (fileEntry == null) {
			return null;
		}
		if (!(FileDomainConstant.FILE_PREVIEW_YES
				.equals(fileEntry.getPreview()) || FileDomainConstant.FILE_PREVIEW_NO
				.equals(fileEntry.getPreview()))) {// 可以预览状态或不可预览状态直接返回
			String state = fileService.obtainFilePreviewStatus(fileEntry
					.getId());
			if (state.equals(FileConstant.FILE_PREVIEW_STATUS_PREVIEWABLE)) {// 可以预览
				fileEntry.setPreview(FileDomainConstant.FILE_PREVIEW_YES);
				fileJpaRepo.save(fileEntry);
			} else if (state
					.equals(FileConstant.FILE_PREVIEW_STATUS_UNPREVIEWABLE)) {// 不可预览
				fileEntry.setPreview(FileDomainConstant.FILE_PREVIEW_NO);
				fileJpaRepo.save(fileEntry);
			}
		}
		return fileEntry;
	}

}
