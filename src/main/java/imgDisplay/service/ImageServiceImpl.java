package imgDisplay.service;

import imgDisplay.dao.Image;
import imgDisplay.repository.ImageRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxUrlWithExpiration;
import com.google.common.io.Files;

@Component("imageService")
@Transactional
public class ImageServiceImpl implements ImageService {

	private final ImageRepository imageRepository;

	@Autowired
	Environment environment;

	@Autowired
	DropBoxService dropBoxService;

	@Autowired
	public ImageServiceImpl(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
	}

	@Override
	public Image getLatestImage() {
		Page<Image> images = imageRepository.findAll(new PageRequest(0, 1,
				Direction.ASC, "id"));
		Image image = images.getContent().get(0);
		return image;
	}

	@Override
	public Image addImageToDropbox(MultipartFile file, String name,
			String comment) {
		DbxEntry.File uploadedFile = uploadFileToDropbox(file, name, comment);
		if (uploadedFile != null) {
			DbxUrlWithExpiration url = dropBoxService.getUrlForFilePath(
					uploadedFile.path, "thius");
			Image image = new Image(new Date(), uploadedFile.path, comment,
					name, url.url, url.expires);
			return imageRepository.save(image);
		} else {
			return null;
		}
	}

	private DbxEntry.File uploadFileToDropbox(MultipartFile file, String name,
			String comment) {
		try {

			// upload to dropBox
			DbxEntry.File uploadedFile = dropBoxService.uploadFile(file, name,
					"thius");

			return uploadedFile;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Page<Image> findImages(int page) {
		Pageable pageSpecification = constructPageSpecification(page);
		// Page<Image> images = this.imageRepository.findAll(pageSpecification);
		// for (Image image : images.getContent()) {
		// if (image.getExpires() != null) {
		// // get from dropbox
		// if (image.getExpires().getTime() < new Date().getTime()) {
		// // refresh the url
		// DbxUrlWithExpiration newUrl = dropBoxService
		// .getUrlForFilePath(image.getImageUrl(), "thius");
		// image.setExpires(newUrl.expires);
		// image.setImageUrl(newUrl.url);
		// imageRepository.save(image);
		// }
		// } else {
		// image.getImageUrl();
		// }
		// }
		return this.imageRepository.findAll(pageSpecification);
	}

	private Pageable constructPageSpecification(int pageIndex) {
		Pageable pageSpecification = new PageRequest(pageIndex, 5, sortByDate());
		return pageSpecification;
	}

	private Sort sortByDate() {
		return new Sort(Sort.Direction.ASC, "uploadTime");
	}

	@Override
	public Page<Image> findImages(ImageSearchCriteria criteria,
			Pageable pageable) {

		Assert.notNull(criteria, "Criteria must not be null");
		String name = criteria.getName();

		if (!StringUtils.hasLength(name)) {
			return this.imageRepository.findAll(null);
		}

		int splitPos = name.lastIndexOf(",");

		if (splitPos >= 0) {
			name = name.substring(0, splitPos);
		}

		return this.imageRepository.findByNameIgnoringCase(name.trim(),
				pageable);
	}

	@Override
	public Image getImage(String name) {
		Assert.notNull(name, "Name must not be null");
		return this.imageRepository.findByNameIgnoringCase(name);
	}

	@Override
	public Image getImageById(Long id) {
		Assert.notNull(id, "Id must not be null");
		return this.imageRepository.findById(id);
	}

	@Override
	public Page<Image> findImages(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image addImage(MultipartFile file, String name, String comment) {
		File newFile = uploadFile(file, name);
		if (newFile != null && newFile.exists()) {
			Image image = new Image(new Date(), newFile.getAbsolutePath(),
					comment, name, "", null);
			return imageRepository.save(image);
		} else {
			return null;
		}
	}

	private File uploadFile(MultipartFile file, String name) {
		try {

			FileInputStream inputStream = (FileInputStream) file
					.getInputStream();
			String imageRoot = environment.getProperty("imageRoot");
			File targetFile = new File(imageRoot, name);
			try {
				byte[] buffer = new byte[inputStream.available()];
				inputStream.read(buffer);
				Files.write(buffer, targetFile);
				System.out.println("Uploaded: " + targetFile.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return targetFile;
		} catch (Exception e) {
			return null;
		}
	}
}
