package com.li.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class InforBean implements Parcelable  {

	/**
	 * SongId：音乐id
	 * SongName : 燕归巢
	 * Artist : 张靓颖;张杰
	 * Album : 燕归巢
	 * AlbumArtist : 张靓颖
	 * Length : 03:31
	 * BitRate : 无损
	 * FlacUrl : http://api.itwusun.com/music/songurl/wy_999_30612754.flac?sign=4cafcd257357f166dd9f8964b0f02b89
	 * AacUrl :
	 * SqUrl : http://api.itwusun.com/music/songurl/wy_320_30612754.mp3?sign=613faef475ecad39d0e25faa27875019
	 * HqUrl : http://api.itwusun.com/music/songurl/wy_160_30612754.mp3?sign=c7f99d244663318998fb8f4e1eb6d09b
	 * LqUrl : http://api.itwusun.com/music/songurl/wy_128_30612754.mp3?sign=59d37c71c61bcc80edc51a72d53bd367
	 * ListenUrl : http://api.itwusun.com/music/songurl/wy_128_30612754.mp3?sign=59d37c71c61bcc80edc51a72d53bd367
	 * CopyUrl : http://api.itwusun.com/music/songurl/wy_320_30612754.mp3?sign=613faef475ecad39d0e25faa27875019
	 * PicUrl : http://api.itwusun.com/music/songurl/wy_320_30612754.jpg?sign=b748bdcf2ac48e83086bb038b1131510
	 * LrcUrl : http://api.itwusun.com/music/songurl/wy_320_30612754.lrc?sign=24dd8d5490750cfb7d05423da75921e9
	 * KlokLrc : http://api.itwusun.com/music/songurl/wy_320_30612754.krc?sign=1257b84ffc43bfe0a0612aee7a416492
	 * Type : wy
	 */

	private int SongId;
	private String SongName;
	private String Artist;
	private String Album;
	private String AlbumArtist;
	private String Length;
	private String BitRate;
	private String FlacUrl;
	private String AacUrl;
	private String SqUrl;
	private String HqUrl;
	private String LqUrl;
	private String ListenUrl;
	private String CopyUrl;
	private String PicUrl;
	private String LrcUrl;
	private String KlokLrc;
	private String Type;

	public int getSongId() {
		return SongId;
	}

	public void setSongId(int songId) {
		SongId = songId;
	}

	public String getSongName() {
		return SongName;
	}

	public void setSongName(String SongName) {
		this.SongName = SongName;
	}


	public String getArtist() {
		return Artist;
	}

	public void setArtist(String Artist) {
		this.Artist = Artist;
	}


	public String getAlbum() {
		return Album;
	}

	public void setAlbum(String Album) {
		this.Album = Album;
	}


	public String getAlbumArtist() {
		return AlbumArtist;
	}

	public void setAlbumArtist(String AlbumArtist) {
		this.AlbumArtist = AlbumArtist;
	}


	public String getLength() {
		return Length;
	}

	public void setLength(String Length) {
		this.Length = Length;
	}


	public String getBitRate() {
		return BitRate;
	}

	public void setBitRate(String BitRate) {
		this.BitRate = BitRate;
	}

	public String getFlacUrl() {
		return FlacUrl;
	}

	public void setFlacUrl(String FlacUrl) {
		this.FlacUrl = FlacUrl;
	}

	public String getAacUrl() {
		return AacUrl;
	}

	public void setAacUrl(String AacUrl) {
		this.AacUrl = AacUrl;
	}

	public String getSqUrl() {
		return SqUrl;
	}

	public void setSqUrl(String SqUrl) {
		this.SqUrl = SqUrl;
	}

	public String getHqUrl() {
		return HqUrl;
	}

	public void setHqUrl(String HqUrl) {
		this.HqUrl = HqUrl;
	}

	public String getLqUrl() {
		return LqUrl;
	}

	public void setLqUrl(String LqUrl) {
		this.LqUrl = LqUrl;
	}

	public String getListenUrl() {
		return ListenUrl;
	}

	public void setListenUrl(String ListenUrl) {
		this.ListenUrl = ListenUrl;
	}

	public String getCopyUrl() {
		return CopyUrl;
	}

	public void setCopyUrl(String CopyUrl) {
		this.CopyUrl = CopyUrl;
	}

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String PicUrl) {
		this.PicUrl = PicUrl;
	}

	public String getLrcUrl() {
		return LrcUrl;
	}

	public void setLrcUrl(String LrcUrl) {
		this.LrcUrl = LrcUrl;
	}

	public String getKlokLrc() {
		return KlokLrc;
	}

	public void setKlokLrc(String KlokLrc) {
		this.KlokLrc = KlokLrc;
	}

	public String getType() {
		return Type;
	}

	public void setType(String Type) {
		this.Type = Type;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		//该方法将类的数据写入外部提供的Parcel中.即打包需要传递的数据到Parcel容器保存，
		// 以便从parcel容器获取数据
		parcel.writeInt(SongId);
		parcel.writeString(SongName);
		parcel.writeString(Artist);
		parcel.writeString(Album);
		parcel.writeString(AlbumArtist);
		parcel.writeString(Length);
		parcel.writeString(BitRate);
		parcel.writeString(FlacUrl);
		parcel.writeString(AacUrl);
		parcel.writeString(SqUrl);
		parcel.writeString(HqUrl);
		parcel.writeString(LqUrl);
		parcel.writeString(ListenUrl);
		parcel.writeString(CopyUrl);
		parcel.writeString(PicUrl);
		parcel.writeString(LrcUrl);
		parcel.writeString(KlokLrc);
		parcel.writeString(Type);

	}
	public static final Creator<InforBean> CREATOR=new Creator<InforBean>() {
		@Override
		public InforBean createFromParcel(Parcel source) {
			//从Parcel容器中读取传递数据值，封装成Parcelable对象返回逻辑层。
			InforBean author=new InforBean();
			author.setSongId(source.readInt());
			author.setSongName(source.readString());
			author.setArtist(source.readString());
			author.setAlbum(source.readString());
			author.setAlbumArtist(source.readString());
			author.setLength(source.readString());
			author.setBitRate(source.readString());
			author.setFlacUrl(source.readString());
			author.setAacUrl(source.readString());
			author.setSqUrl(source.readString());
			author.setHqUrl(source.readString());
			author.setLqUrl(source.readString());
			author.setListenUrl(source.readString());
			author.setCopyUrl(source.readString());
			author.setPicUrl(source.readString());
			author.setLrcUrl(source.readString());
			author.setKlokLrc(source.readString());
			author.setType(source.readString());
			return author;
		}

		@Override
		public InforBean[] newArray(int size) {
			//创建一个类型为T，长度为size的数组，仅一句话（return new T[size])即可。方法是供外部类反序列化本类数组使用。
			return new InforBean[size];
		}
	};
}
