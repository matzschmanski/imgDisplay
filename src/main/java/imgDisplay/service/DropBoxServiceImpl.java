package imgDisplay.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxUrlWithExpiration;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;

@Component("dropBoxService")
public class DropBoxServiceImpl implements DropBoxService {

	protected DbxWebAuthNoRedirect webAuth;
	protected DbxRequestConfig config;

	@Override
	public String getCodeUrl() {

		// Have the user sign in and authorize your app.
		String authorizeUrl = getWebAuth().start();
		return authorizeUrl;
	}

	@Override
	public boolean finnishAuth(String user, String code) throws DbxException {
		DbxAuthFinish authFinish = getWebAuth().finish(code);
		String accessToken = authFinish.accessToken;
		System.setProperty("DROPBOX_" + user.toUpperCase(), accessToken);
		System.out.println(getAccessTokenForUser(user));
		return true;
	}

	private DbxRequestConfig getConfig() {
		if (this.config == null) {
			getWebAuth();
		}
		return this.config;
	}

	private DbxWebAuthNoRedirect getWebAuth() {
		if (webAuth == null) {
			String APP_KEY = System.getenv("DROPBOX_APP_KEY");
			String APP_SECRET = System.getenv("DROPBOX_APP_SECRET");
			File props = new File("C:/Entwicklung/conf.properties");
			if (props.exists()) {
				Properties prop = new Properties();
				InputStream in;
				try {
					in = new FileInputStream(props);
					prop.load(in);
					in.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (APP_KEY == null || "".equals(APP_KEY)) {
					APP_KEY = prop.getProperty("appkey");
				}
				if (APP_SECRET == null || "".equals(APP_SECRET)) {
					APP_SECRET = prop.getProperty("secret");

				}
			}

			DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

			this.config = new DbxRequestConfig("ThamejaRequest/1.0", Locale
					.getDefault().toString());
			this.webAuth = new DbxWebAuthNoRedirect(config, appInfo);

		}
		return webAuth;
	}

	@Override
	public DbxEntry.File uploadFile(MultipartFile file, String name, String user)
			throws DbxException, IOException {
		DbxEntry.File uploadedFile = null;
		DbxClient client = new DbxClient(getConfig(),
				getAccessTokenForUser(user));
		System.out.println("Uploading account: "
				+ client.getAccountInfo().displayName);
		FileInputStream inputStream = (FileInputStream) file.getInputStream();
		try {
			uploadedFile = client.uploadFile("/" + name, DbxWriteMode.add(),
					file.getSize(), inputStream);
			System.out.println("Uploaded: " + uploadedFile.toString());
			DbxEntry entry = client.getMetadata(uploadedFile.path);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return uploadedFile;
	}

	@Override
	public DbxUrlWithExpiration getUrlForFilePath(String path, String user) {
		DbxClient client = new DbxClient(getConfig(),
				getAccessTokenForUser(user));
		DbxUrlWithExpiration urlWithExpiration = null;
		try {
			urlWithExpiration = client.createTemporaryDirectUrl(path);
		} catch (DbxException e) {
			e.printStackTrace();
		}
		return urlWithExpiration;
	}

	private String getAccessTokenForUser(String user) {
		return System.getProperty("DROPBOX_" + user.toUpperCase(), "");
	}

}
