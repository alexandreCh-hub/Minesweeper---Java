enum Direction{
	TOP,
	TOPRIGHT,
	RIGHT,
	BOTTOMRIGHT,
	BOTTOM,
	BOTTOMLEFT,
	LEFT,
	TOPLEFT;

	//recupere la direction opposée qui sera utilisée pour definir les cases voisines
    public Direction getOpposite(){
	switch(this){
	case TOP:
	    return BOTTOM;
	case TOPRIGHT:
	    return BOTTOMLEFT;
	case RIGHT:
	    return LEFT;
	case BOTTOMRIGHT:
	    return TOPLEFT;
	case BOTTOM:
	    return TOP;
	case BOTTOMLEFT:
	    return TOPRIGHT;
	case LEFT:
	    return RIGHT;
	case TOPLEFT:
	    return BOTTOMRIGHT;
	}
	throw new IllegalArgumentException("Aucun opposé trouvé");
    }
}
