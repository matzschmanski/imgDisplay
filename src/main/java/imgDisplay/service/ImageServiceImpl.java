package imgDisplay.service;

import imgDisplay.dao.Image;
import imgDisplay.repository.ImageRepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

@Component("imageService")
@Transactional
public class ImageServiceImpl implements ImageService {

	private final ImageRepository imageRepository;

	@Autowired
	Environment environment;

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
	public Image addImage(MultipartFile file, String name, String comment) {
		String url = writeFileToImgStorage(file, name, comment);
		Image newImage = new Image(new Date(), url, comment, name);
		return imageRepository.save(newImage);
	}

	private String writeFileToImgStorage(MultipartFile file, String name,
			String comment) {
		try {
			byte[] bytes = file.getBytes();
			File root = new File(environment.getProperty("imageRoot"));
			root.mkdirs();
			Date fileName = new Date();
			File target = new File(root, fileName.getTime() + "");
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(target));
			stream.write(bytes);
			stream.close();
			return target.getAbsolutePath();
		} catch (Exception e) {
			return "You failed to upload " + name + " => " + e.getMessage();
		}
	}

	@Override
	public Page<Image> findImages(int page) {
		Pageable pageSpecification = constructPageSpecification(page);
		return this.imageRepository.findAll(pageSpecification);
	}

	private Pageable constructPageSpecification(int pageIndex) {
		Pageable pageSpecification = new PageRequest(pageIndex, 1, sortByDate());
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
}
