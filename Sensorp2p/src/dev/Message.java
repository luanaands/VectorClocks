package dev;
import java.util.Arrays;

public class Message {

	private int origem;
	private int clockOrigem;
	private String conteudo;
	private int[] timestamp;
	
	public int getClockOrigem() {
		return clockOrigem;
	}
	public void setClockOrigem(int clockOrigem) {
		this.clockOrigem = clockOrigem;
	}
	
	public int[] getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int[] timestamp) {
		this.timestamp = timestamp;
	}
	public int getOrigem() {
		return origem;
	}
	public void setOrigem(int origem) {
		this.origem = origem;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	
	public String toString(){
		return origem + ";" + conteudo+ ";" + clockOrigem + ";" + Arrays.toString(timestamp);
	}	
	
	public void trataMensagem(String msg) {
		String[] msg2 = msg.split(";");
		this.origem = Integer.parseInt(msg2[0]);
		this.conteudo= msg2[1];
		this.clockOrigem = Integer.parseInt(msg2[2]);
		this.timestamp = stringToTimestamp(msg2[3]);
	}
	
	public int[] stringToTimestamp(String tmp){
		tmp = tmp.replace("[","");
		tmp = tmp.replace("]","");
		tmp = tmp.replace(" ","");
		
		String str[] = tmp.split(",");
		int[] timestamp = new int[str.length];
		
		for(int i = 0; i < str.length; i++) {
			timestamp[i] = Integer.parseInt(str[i]);
		}
		
		return timestamp;
		
	}
}
