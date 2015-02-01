package imgDisplay.service;

import imgDisplay.dao.Image;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

	Page<Image> findImages(ImageSearchCriteria criteria, Pageable pageable);

	Image getImage(String name);

	Image addImage(MultipartFile file, String name, String comment);

	Image getLatestImage();

	Image getImageById(Long id);

	Page<Image> findImages(Pageable pageable);

	Page<Image> findImages(int page);

}
