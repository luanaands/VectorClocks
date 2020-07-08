package dev;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;

public class Channel {

	int idcanalProcesso;
	private MulticastSocket multisocket;
	private DatagramSocket socket;
	private boolean running;
	private int[] clocks;

	private LinkedTransferQueue<Message> input;
	private LinkedTransferQueue<Message> output;
	private List<Message> wait;

//	public EventClocks() {

//	}
	public LinkedTransferQueue<Message> getInput() {
		return this.input;
	}

	public LinkedTransferQueue<Message> getOuput() {
		return this.output;
	}

	public void Map(int p, int id) {
		this.idcanalProcesso = id;
		wait = new ArrayList<Message>();
		clocks = new int[p];
		for (int i = 0; i < clocks.length; i++) {
			clocks[i] = 0;
		}

	}

	public void bind(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}

	public void bindGroup(int port) throws IOException {
		multisocket = new MulticastSocket(port);
	}

	public void JoinGroup(InetAddress address) throws IOException {
		multisocket.joinGroup(address);
	}

	public void settingClock(int[] timestamp) {
		for (int i = 0; i < clocks.length; i++) {
			clocks[i] = Math.max(clocks[i], timestamp[i]);
		}
	}

	// close the socket
	public void stop() {
		running = false;
		socket.close();
		System.out.println("Closed");
	}

	public void recebe() {
		input = new LinkedTransferQueue<Message>();
		
		
		new Thread() {
			
			@Override
			public void run() {

				byte[] buffer = new byte[1024];

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

				running = true;
				int c = 0;
				while (running) {
					try {
						multisocket.receive(packet);
						
						Message m = new Message();
						m.trataMensagem(new String(packet.getData(), 0, packet.getLength()));

						if (m.getOrigem() == 1 && c < 6) {
							try {
								System.out.println("Atrasa");
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							c++;
						}
						c= 0;
						
                        if(m.getOrigem() != idcanalProcesso) {
                        	
                        	if (Aceite(m.getTimestamp(), m.getOrigem()) ) {

    							settingClock(m.getTimestamp());

    							Print(m, 1);
    							input.put(m);

    							if (!wait.isEmpty()) {

    								boolean controle = true;

    								while (controle) {
    									controle = false;
    									Message msg = new Message();
    									for (int i = 0; i < wait.size(); i++) {
    										msg = wait.get(i);
    										if (Aceite(msg.getTimestamp(), msg.getOrigem())) {
    											settingClock(msg.getTimestamp());
    											Print(msg, 3);
    											input.put(msg);
    											wait.remove(i);
    											controle = true;
    											break;
    										}
    									}
    								}
    							}
    						} else {
    						wait.add(m);
    						Print(m, 2);
    							
    						}
                        	
                        	
                        }
						

					} catch (IOException e) {
						break;
					}
				}

			}
		}.start();
	}

	public void Print(Message m, int status) {
		if (status == 1) {
			System.out.println("Caso Normal \t" + idcanalProcesso + ";" + m.getOrigem() + "\t"
					+ Arrays.toString(clocks) + "\t" + Arrays.toString(m.getTimestamp()) + " Entregue");
		} else if (status == 2){
			System.out.println("Pendente \t" + idcanalProcesso + ";" + m.getOrigem() + "\t"
					+ Arrays.toString(clocks) + "\t" + Arrays.toString(m.getTimestamp()) + " *Fila " + wait.size());

		}else {
			System.out.println("Atrasada \t" + idcanalProcesso + ";" + m.getOrigem() +  "\t"
					+ Arrays.toString(clocks) + "\t" + Arrays.toString(m.getTimestamp()) + " Entregue");
		}

	}

	public boolean Aceite(int[] timestamp, int pOrigem) {
      System.out.println(pOrigem +  Arrays.toString(clocks) + " - " + Arrays.toString(timestamp) + idcanalProcesso );
		boolean status = false;
		if (timestamp[pOrigem] != clocks[pOrigem] + 1) {
			return status;
		} else {

			for (int i = 0; i < clocks.length; i++) {
				if (i != pOrigem) {
					status = true;
					if (timestamp[i] > clocks[i]) {
						status = false;
						return status;
					}
				}
			}
		}
		return status;
	}

	public void Send(InetAddress address, int port) {
		output = new LinkedTransferQueue<Message>();
		new Thread() {
			@Override
			public void run() {
				while (true) {
					if (!output.isEmpty()) {
						try {

							Message msg = output.take();
							clocks[msg.getOrigem()] = msg.getClockOrigem();
							msg.setTimestamp(clocks);
							DeliverGroup(address, port, msg);

						} catch (InterruptedException | IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	// MultiCast
	public void DeliverGroup(InetAddress address, int port, Message msg) throws IOException {
		DatagramPacket packet = new DatagramPacket(msg.toString().getBytes(), msg.toString().length(), address, port);
		multisocket.send(packet);
	}

}
