package dev;

import java.io.IOException;
import java.net.InetAddress;

public class SocketPeer {

	public static void main(String[] args) throws IOException {

		int sourcePort = 444;
		String group = "224.0.0.3";
		
		InetAddress addr = InetAddress.getByName(group);		
        int numeroSensor = 3;
        System.out.println("Iniciando sensores " +  numeroSensor);
        Sensor sensores[] = new Sensor[numeroSensor];
        for(int n = 0 ; n < numeroSensor ; n++) {
        	sensores[n] = new Sensor(n, sourcePort, addr, numeroSensor);
        	sensores[n].Consumir();
			sensores[n].Produzir();
        }
	}
}
