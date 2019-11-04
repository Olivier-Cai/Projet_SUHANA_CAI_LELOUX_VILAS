package SGBD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
	private ArrayList<HeapFile> heapFiles;
	
	private FileManager() {
	}
	
	private static FileManager fileManager = null;

	public static FileManager getInstance() {
		if (fileManager == null)
			fileManager = new FileManager();
		return fileManager;
	}
	
	public void init() {
		for(int i = 0; i < DBDef.getInstance().getListRel().size(); i++) {
			heapFiles.add(new HeapFile(DBDef.getInstance().getListRel().get(i)));
		}
	}
	
	public void CreateRelationFile(RelDef relDef) throws IOException {
		HeapFile hF =new HeapFile(relDef);
		heapFiles.add(hF);
		hF.createNewOndisk();
	}
	
	public Rid insertRecordInRelation(Record record ,String relName) {
		Rid rid = null;
		for(int i = 0; i < heapFiles.size(); i++) {
			if (heapFiles.get(i).getReldef().getRelname().equals(relName)) {
				rid = heapFiles.get(i).insertRecord(record);
				break;
			}
		}
		return rid;
	}
	
	public List<Record> selectAllFromRelation(String relName){
		List<Record> list = new ArrayList<Record>();
		for(int i = 0; i < heapFiles.size(); i++) {
			if (heapFiles.get(i).getReldef().getRelname().equals(relName)) 
				list =heapFiles.get(i).getAllRecords();
			}
		return list;
	}
	
	public List<Record> selectFromRelation(String relName, int idxCol, String valeur){
		List<Record> list = new ArrayList<Record>();
		List<Record> list2 = new ArrayList<Record>();
		for(int i = 0; i < heapFiles.size(); i++) {
			if (heapFiles.get(i).getReldef().getRelname().equals(relName)) 
				list =heapFiles.get(i).getAllRecords();
			}
		for(int i = 0; i<list.size();i++) {
			if(list.get(i).getValues().get(idxCol).equals(valeur))
				list2.add(list.get(i));
		}
		return list2;
	}
	

}