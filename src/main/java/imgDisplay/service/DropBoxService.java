package imgDisplay.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxUrlWithExpiration;

public interface DropBoxService {

	String getCodeUrl();

	boolean finnishAuth(String user, String code) throws DbxException;

	DbxEntry.File uploadFile(MultipartFile file, String name, String user)
			throws DbxException, IOException;

	DbxUrlWithExpiration getUrlForFilePath(String path, String user);
}
