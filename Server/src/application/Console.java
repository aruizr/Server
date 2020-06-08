package application;

public class Console {

	public Console() {
		// TODO Auto-generated constructor stub
	}
	
	public void command(String text) {
		if (text.charAt(0) == '/') {
			switch (text.substring(1)) {
			case "start":
				ServerController.getInstance().log(text);
				ServerController.getInstance().start();
				break;
				
			case "stop":
				ServerController.getInstance().log(text);
				ServerController.getInstance().stop();
				break;
				
			case "update":
				ServerController.getInstance().log(text);
				ServerController.getInstance().update();
				break;
				
			case "exit":
				ServerController.getInstance().log(text);
				ServerController.getInstance().exit();
				break;

			default:
				return;
			}
		}
	}
}
