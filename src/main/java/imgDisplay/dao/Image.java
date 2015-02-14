package imgDisplay.dao;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Image implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private Date uploadTime;

	@Column(nullable = false)
	private String imageUrl;

	@Column(nullable = true)
	private String comment;

	@Column(nullable = false)
	private String name;

	@Column
	private String temporaryUrl;

	@Column
	private Date expires;

	protected Image() {
		// no-args constructor required by JPA spec
		// this one is protected since it shouldn't be used directly
	}

	public Image(Date uploadTime, String imageUrl, String comment, String name,
			String temporaryUrl, Date expires) {
		super();
		this.uploadTime = uploadTime;
		this.imageUrl = imageUrl;
		this.comment = comment;
		this.name = name;
		this.temporaryUrl = temporaryUrl;
		this.expires = expires;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getImageUrl() {
		String urlAsString = "http://178.62.232.129:8080/imgDisplay/imageStore/"
				+ name;
		return urlAsString;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTemporaryUrl() {
		return temporaryUrl;
	}

	public void setTemporaryUrl(String temporaryUrl) {
		this.temporaryUrl = temporaryUrl;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

}
