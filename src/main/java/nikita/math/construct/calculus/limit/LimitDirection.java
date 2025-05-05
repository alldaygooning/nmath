package nikita.math.construct.calculus.limit;

public enum LimitDirection {
	NONE(""),
	LEFT_TO_RIGHT("-1"),
	RIGHT_TO_LEFT("1");
	
	private final String direction;
	
	LimitDirection(String direction){
		this.direction = direction;
	}
	
	public String getDirection() {
		return direction;
	}
}
