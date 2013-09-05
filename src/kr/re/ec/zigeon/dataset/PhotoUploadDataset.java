package kr.re.ec.zigeon.dataset;

/**************** Simple Dataset for Image upload *******************/
public class PhotoUploadDataset {
	public int type;
	public int idx;
	public String sourcePath;
	
	/**
	 * @Author KimTaehee
	 * 
	 * @param type
	 * type refer to Constants: MSG_TYPE_LANDMARK | MSG_TYPE_POSTING
	 * @param idx
	 * idx is each type's idx
	 * @param sourcePath
	 * sourcePath ex: storage/sdcard0/picture/hanwoo.jpg (from WriteActivities)
	 */
	public PhotoUploadDataset(int _type, int _idx, String _sourcePath) {
		this.type = _type;
		this.idx = _idx;
		this.sourcePath = _sourcePath;
	}
}

