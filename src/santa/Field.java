package santa;

class Field {
    private Child child;
    private boolean hasGift;
    private boolean hasSanta;

    public Field(boolean hasSanta, boolean hasGift, Child child) {
        this.hasSanta = hasSanta;
        this.hasGift = hasGift;
        this.child = child;
    }

    public void setChild(Child child)  { this.child = child; }
    public Child getChild() { return child; }

    public boolean hasSanta() { return hasSanta; }
    public boolean hasGift() { return hasGift; }
    public boolean hasChild() { return child != null; }

    public void setHasSanta(boolean hasSanta) { this.hasSanta = hasSanta; }
    public void setHasGift(boolean hasGift) { this.hasGift = hasGift; }
}