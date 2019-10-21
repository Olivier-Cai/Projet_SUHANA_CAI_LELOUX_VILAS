package SGBD;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Record {

	private RelDef reldef;
	private List<String> values;

	public Record(RelDef reldef) {
		this.reldef = reldef;
		values = new ArrayList<>();
	}

	public RelDef getReldef() {
		return reldef;
	}

	public void setReldef(RelDef reldef) {
		this.reldef = reldef;
	}

	public List<String> getValues() {
		return values;
	}

	public void writeToBuffer(ByteBuffer buffer, int position) {
		String string;
		List<String> list = reldef.getList();

		for (int i = 0; i < reldef.getNumcol(); i++) {
			string = list.get(i);

			if (string.startsWith("int")) {
				buffer.putInt(position, Integer.parseInt(values.get(i)));
				position += Integer.BYTES;
			} else if (string.startsWith("float")) {
				buffer.putFloat(position, Float.parseFloat(values.get(i)));
				position += Float.BYTES;
			} else if (string.startsWith("string")) {
				StringTokenizer st = new StringTokenizer(string, "string");
				int sizeString = Integer.parseInt(st.nextToken().toString());
				for (int j = 0; j < sizeString; j++) {
					buffer.putChar(position, values.get(i).charAt(j));
					position += Character.BYTES;
				}

			}
		}

	}

	public void readFromBuffer(ByteBuffer buffer, int position) {
		String string;
		List<String> list = reldef.getList();

		for (int i = 0; i < reldef.getNumcol(); i++) {
			string = list.get(i);

			if (string.startsWith("int")) {
//				System.out.println(buffer.getInt(position));
				values.add(Integer.toString(buffer.getInt(position)));
				position += Integer.BYTES;

			} else if (string.startsWith("float")) {
				buffer.getFloat(position);
				values.add(Float.toString(buffer.getFloat(position)));
				position += Float.BYTES;
			} else if (string.startsWith("string")) {

				StringTokenizer st = new StringTokenizer(string, "string");
				int sizeString = Integer.parseInt(st.nextToken().toString());
				StringBuffer sb = new StringBuffer();
				
				for (int j = 0; j < sizeString; j++) {
					sb.append(buffer.getChar(position));
					position += Character.BYTES;
				}
				
				values.add(sb.toString());
			}
		}

	}

}
