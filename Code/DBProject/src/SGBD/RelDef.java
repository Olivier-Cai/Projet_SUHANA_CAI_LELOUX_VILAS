package SGBD;

import java.util.List;


public class RelDef {

	private String relname;
	private int numcol;
	private List <String> list;
	private int fileIdx;
	private int recordSize;
	private int slotCount;


	public RelDef(String relname, int numcol, List<String> list,int fileIdx, int recordSize, int slotCount) {
		this.relname = relname;
		this.numcol = numcol;
		this.list = list ;
		this.fileIdx = fileIdx;
		this.recordSize = recordSize;
		this.slotCount = slotCount;
	}

	public List<String> getList() {
		return this.list;
	}

	public int getNumcol() {
		return this.numcol;
	}

	public int getSlotCount() {
		return slotCount;
	}	

	@Override
	public String toString() {
		return "*"+relname+"*"+numcol+"*"+list+"*"+fileIdx+"*"+recordSize+"*"+slotCount+"*";
	}

	public int getFileIdx() {
		return fileIdx;
	}
	
	public int getRecordSize() {
		return recordSize;
	}
	
	public String getRelname() {
		return relname;
	}
	
}
