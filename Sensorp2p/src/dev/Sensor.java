package dev;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

public class Sensor {

	int id;
	private String name;
	private Random random;
	private Channel channel;
	int clock;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Random getRandom() {
		return random;
	}
	
	// set a name arbitrary
	public Sensor(int id,int port, InetAddress add, int np) throws IOException {
		// set a name arbitrary
		
		String tag = String.valueOf(id);
		this.id = id; 
		this.setName("Sensor-" + tag);
		
		channel = new Channel();
		channel.bindGroup(port);
		channel.JoinGroup(add);
		channel.Map(np, this.id);
		
		System.out.println("Sensor iniciado Id: " + getName());
		random = new Random();
		
		channel.Send(add, port);
		channel.recebe();
		
	}
	
	// get a value arbitrary
	public String Randomize() {
		return String.valueOf(getRandom().nextInt());
	}
	
  public void Produzir() {
	  
	  new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					String value = getName() + ">>> " + Randomize();

					String[] number = value.split(" ");
					// System.out.println(number[0]);

					if (Integer.parseInt(number[1]) == 0 || value.isEmpty()) {
						break;
					}

					clock++;
					Message m = new Message();        
	            	m.setOrigem(id);
	            	m.setConteudo(Randomize());      
	            	m.setClockOrigem(clock);
					channel.getOuput().offer(m);

				}
			}
		}.start();
  }
	
 public void Consumir() {
	 new Thread() {
			@Override
			public void run() {
				while (true) {
					if (!channel.getInput().isEmpty()) {

						try {
							Message message = channel.getInput().take();         
							//System.out.println("o" + channel.getInput().take());
	
						} catch (InterruptedException e1) {
					
							e1.printStackTrace();
						}

					}

				}
			}
		}.start();
 	}
	
}
