package SGBD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DBManager {

	private DBManager() {

	}

	private static DBManager dbManager = null;

	public static DBManager getInstance() {
		if (dbManager == null)
			dbManager = new DBManager();
		return dbManager;
	}

	public void init() throws IOException {
		DBDef.getInstance().init();
		FileManager.getInstance().init();
	}

	public void finish() throws IOException {
		BufferManager.getInstance().flushAll();
		DBDef.getInstance().finish();
	}

	public void processCommand(String commande) throws IOException {
		StringTokenizer st = new StringTokenizer(commande);
		String nomCommand = st.nextToken();
		String relname;

		switch (nomCommand) {
		case "create":
			relname = st.nextToken();
			int numcol = Integer.parseInt(st.nextToken());
			List<String> list = new ArrayList<String>();

			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}

			creatRelation(relname, numcol, list);
			break;

		case "insert":
			relname = st.nextToken();
			List<String> listValues = new ArrayList<String>();
			while (st.hasMoreTokens()) {
				listValues.add(st.nextToken());
			}
			insert(relname, listValues);
			break;

		case "clean":
			clean();
			break;

		case "selectall":
			relname = st.nextToken();
			selectAll(relname);
			break;

		case "select":
			relname = st.nextToken();
			select(relname, Integer.parseInt(st.nextToken()), st.nextToken());
			break;
		case "insertall":
			relname = st.nextToken();
			String filename = st.nextToken();
			insertAll(relname, filename);
			break;
		case "delete":
			relname = st.nextToken();
			delete(relname, Integer.parseInt(st.nextToken()), st.nextToken());
			break;
		case "join":
			relname = st.nextToken();
			join(relname,st.nextToken(),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()));
			break;
		case "createindex":
			relname = st.nextToken();
			createIndex(relname , Integer.parseInt(st.nextToken()) ,Integer.parseInt(st.nextToken()));
			break;
		case "selectindex":
			relname = st.nextToken();
			selectIndex(relname, Integer.parseInt(st.nextToken()) ,Integer.parseInt(st.nextToken()));
		}

	}

	public void creatRelation(String relname, int numcol, List<String> list) throws IOException {
		int recordSize = 0;

		for (int i = 0; i < numcol; i++) {
			String string = list.get(i);
			if (string.startsWith("int")) {
				recordSize += Integer.BYTES;
			} else if (string.startsWith("float")) {
				recordSize += Float.BYTES;
			} else if (string.startsWith("string")) {
				recordSize += Character.BYTES * Integer.parseInt(string.split("string")[1]);
			}

		}

		int slotCount = Constants.PAGE_SIZE / (recordSize + 1);
		RelDef reldef = new RelDef(relname, numcol, list, DBDef.getInstance().getCompteur(), recordSize, slotCount);

		DBDef.getInstance().addRelation(reldef);
		FileManager.getInstance().CreateRelationFile(reldef);
	}

	private void clean() {
		File fichier = new File(Constants.PATH + "/Catalogue.def");
		fichier.delete();
		boolean test = true;
		int i = 0;
		while (test) {
			File fichier2 = new File(Constants.PATH + "/Data_" + i + ".rf");
			test = fichier2.delete();
			i++;
		}
		BufferManager.getInstance().reset();
		DBDef.getInstance().reset();
		FileManager.getInstance().reset();
	}

	private void selectAll(String relName) throws IOException {
		List<Record> list = FileManager.getInstance().selectAllFromRelation(relName);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		System.out.println("Total records = " + list.size());

	}

	public void select(String relName, int idxCol, String valeur) throws IOException {
		List<Record> list = FileManager.getInstance().selectFromRelation(relName, idxCol, valeur);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		System.out.println("Total records = " + list.size());
	}

	public void delete(String relName, int idxCol, String valeur) throws IOException {
		int deletedRecords = FileManager.getInstance().deletedFromRelation(relName, idxCol, valeur);
		System.out.println("Total deleted records = " + deletedRecords);
	}

	public void insert(String nomRelation, List<String> values) throws IOException {
		Record record = null;
		RelDef relation = null;

		for (int i = 0; i < FileManager.getInstance().getHeapFiles().size(); i++) {
			if (FileManager.getInstance().getHeapFiles().get(i).getReldef().getRelname().equals(nomRelation))
				relation = FileManager.getInstance().getHeapFiles().get(i).getReldef();
		}

		record = new Record(relation, values);
		FileManager.getInstance().insertRecordInRelation(record, relation.getRelname());
	}

	public void insertAll(String nomRelation, String fileName) throws IOException {
		RelDef relation = null;
		File file = new File(Constants.PATH + "/../" + fileName);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String row;

			for (int i = 0; i < FileManager.getInstance().getHeapFiles().size(); i++) {
				if (FileManager.getInstance().getHeapFiles().get(i).getReldef().getRelname().equals(nomRelation))
					relation = FileManager.getInstance().getHeapFiles().get(i).getReldef();
			}

			while ((row = br.readLine()) != null) {
				List<String> values = new ArrayList<>();
				String[] data = row.split(",");

				for (int i = 0; i < data.length; i++) {
					values.add(i, data[i]);
				}

				insert(relation.getRelname(), values);

			}
		}

	}

	
	public void createIndex(String relName, int indCol, int ordre) {
		FileManager.getInstance().createTree(relName, indCol, ordre);
	}
	
	public void selectIndex(String relname, int indCol, int valeur) {
		int totalRecord = FileManager.getInstance().selectindex(relname, indCol, valeur);
		System.out.println("Totale records = " + totalRecord);
	}
	
	public void join(String nomRel1, String nomRel2, int idxColRel1, int idxColRel2) {
		try {
			int resultat = FileManager.getInstance().join(nomRel1, nomRel2, idxColRel1, idxColRel2);
			System.out.println("Total tuple(s) = " + resultat);
		} catch (IOException e) {
			System.out.println("resultat null");
			e.printStackTrace();
		}
	}

}
