package flappy;

public class Parameters implements Cloneable {
	
	double [] pars;
	int num;
	
	static double random() {
		return Math.random() * ((Math.random() > 0.5) ? 1 : -1);
	}
	
	public Parameters(int num) {
		this.num = num;
		pars = new double[num];
		for(int i = 0; i<num; i++)
			pars[i] = random();
	}
	public Parameters clone() {
		try {
			return (Parameters)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
