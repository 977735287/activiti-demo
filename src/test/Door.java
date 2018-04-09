package test;

interface doorInter{
	void theftproof();
	void waterproof();
	void bulletproof();
}

class Common1 {
	public void openDoor(){}
	public void closeDoor(){}
}

public class Door extends Common1 implements doorInter {
	public void theftproof(){}
	public void waterproof(){}
	public void bulletproof(){}
}

