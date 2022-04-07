package santa;

import java.util.Collection;
import java.util.Random;
import java.util.ArrayList;

public class Child implements Runnable{
    public enum Activity {
        SLEEPING, CHASING, PLAYING, ENJOYING_THE_GIFT;
    };

    private Board board;
    private SantaClaus santa;

    Position position;
    private Activity activity;
    private long activityTime;
    private long activityEndTime;
    private long activityStartTime;

    private Random numberGenerator;

    private static final int MAX_ACTIVITY_TIME = 5000;
    private static final int MIN_ACTIVITY_TIME = 2500;
    private static final int SIGHT_DISTANCE = 3;
    private static final int CHASE_MODE_MILLISECONDS_PER_MOVE = 500;

    public Child(Position position, Board board, SantaClaus santa) {
        numberGenerator = new Random();
        this.board = board;
        this.santa = santa;
        activity = Activity.PLAYING;
        setActivityTime();
        this.position = position;
        board.getField(position).setChild(this);
    }

    public Activity getActivity() { return activity; }

    private void setActivityTime() {
        activityTime = numberGenerator.nextInt(MAX_ACTIVITY_TIME-MIN_ACTIVITY_TIME)+MIN_ACTIVITY_TIME;
        activityStartTime = System.currentTimeMillis();
        activityEndTime =  activityStartTime + activityTime;
    }

    private void findNextActivity() {
        ArrayList<Position> neighbours = board.getNeighboursPositions(position, SIGHT_DISTANCE);

        if(isSantaInTheCollection(neighbours)) {
            activity = Activity.CHASING;
        } else {
            activity = Activity.PLAYING;
        }
    }

    private boolean isSantaInTheCollection(Collection<Position> coll) {
        for(Position p : coll) {
            if(board.getField(p).hasSanta()) {
                return true;
            }
        }
        return false;
    }

    private Position getGiftPosition(Collection<Position> coll) throws Exception {
        for(Position p : coll) {
            if(board.getField(p).hasGift() && !board.getField(p).hasChild()) {
                return p;
            }
        }
        throw new Exception("No gift found");
    }

    private void doRandomMove() {
        int rand = numberGenerator.nextInt(4);
        switch(rand) {
            case 0: {
                if(movePossible(Direction.UP)) {
                    this.move(board.getPositionOfANeighbour(position, Direction.UP));
                    break;
                }
            }
            case 1: {
                if(movePossible(Direction.LEFT)) {
                    this.move(board.getPositionOfANeighbour(position, Direction.LEFT));
                    break;
                }
            }
            case 2: {
                if(movePossible(Direction.DOWN)) {
                    this.move(board.getPositionOfANeighbour(position, Direction.DOWN));
                    break;
                }
            }
            case 3: {
                if(movePossible(Direction.RIGHT)) {
                    this.move(board.getPositionOfANeighbour(position, Direction.RIGHT));
                    break;
                }
            }
            default: {
                if(movePossible(Direction.UP)) {
                    this.move(board.getPositionOfANeighbour(position, Direction.UP));
                } else if(movePossible(Direction.LEFT)) {
                    this.move(board.getPositionOfANeighbour(position, Direction.LEFT));
                } else if(movePossible(Direction.DOWN)) {
                    this.move(board.getPositionOfANeighbour(position, Direction.DOWN));
                } else if(movePossible(Direction.RIGHT)) {
                    this.move(board.getPositionOfANeighbour(position, Direction.RIGHT));
                }
            }
        }
    }

    private void doChaseMove() {
        int currentX = position.getX(), currentY = position.getY();
        int santaX = santa.getPosition().getX(), santaY = santa.getPosition().getY();
        Direction newDireciton = null;
        int diffX = currentX - santaX;
        int diffY = currentY - santaY;

        if(Math.min(Math.abs(diffX), board.getMAX_X() - Math.abs(diffX))
                > Math.min(Math.abs(diffY), board.getMAX_Y() - Math.abs(diffY))) {
            if(((diffX > 0 && diffX < board.getMAX_X()/2) ||
                    diffX < 0 && Math.abs(diffX) > board.getMAX_X()/2) &&
                    movePossible(Direction.LEFT)) {
                newDireciton = Direction.LEFT;
            } else if (((diffX < 0 && Math.abs(diffX) < board.getMAX_X()/2) ||
                    diffX > 0 && diffX > board.getMAX_X()/2) &&
                    movePossible(Direction.RIGHT)) {
                newDireciton = Direction.RIGHT;
            } else if(((diffY > 0 && diffY < board.getMAX_Y()/2) ||
                    diffY < 0 && Math.abs(diffY) > board.getMAX_Y()/2) &&
                    movePossible(Direction.UP)) {
                newDireciton = Direction.UP;
            } else if(((diffY < 0 && Math.abs(diffY) < board.getMAX_Y()/2) ||
                    diffY > 0 && diffY > board.getMAX_Y()/2) &&
                    movePossible(Direction.DOWN)) {
                newDireciton = Direction.DOWN;
            }
        } else {
            if(((diffY > 0 && diffY < board.getMAX_Y()/2) ||
                    diffY < 0 && Math.abs(diffY) > board.getMAX_Y()/2) &&
                    movePossible(Direction.UP)) {
                newDireciton = Direction.UP;
            } else if(((diffY < 0 && Math.abs(diffY) < board.getMAX_Y()/2) ||
                    diffY > 0 && diffY > board.getMAX_Y()/2) &&
                    movePossible(Direction.DOWN)) {
                newDireciton = Direction.DOWN;
            }else if(((diffX > 0 && diffX < board.getMAX_X()/2) ||
                    diffX < 0 && Math.abs(diffX) > board.getMAX_X()/2) &&
                    movePossible(Direction.LEFT)) {
                newDireciton = Direction.LEFT;
            } else if (((diffX < 0 && Math.abs(diffX) < board.getMAX_X()/2) ||
                    diffX > 0 && diffX > board.getMAX_X()/2) &&
                    movePossible(Direction.RIGHT)) {
                newDireciton = Direction.RIGHT;
            }
        }

        if(movePossible(newDireciton)) {
            move(board.getPositionOfANeighbour(position, newDireciton));
        }
    }

    private boolean movePossible(Direction direction) {
        if(direction == null) return false;
        return !board.getField(board.getPositionOfANeighbour(position, direction)).hasChild();
    }

    private void move(Position p) {
        board.getField(position).setChild(null);
        position = p;
        board.getField(position).setChild(this);
    }


    @Override
    public void run() {
        while(!board.isSantaCaught() || activity != Activity.ENJOYING_THE_GIFT) {

            // wait
            if(activity == Activity.PLAYING || activity == Activity.SLEEPING) {
                while (System.currentTimeMillis() < activityEndTime) {
                    try {
                        Thread.currentThread().sleep(activityEndTime - System.currentTimeMillis());
                    } catch (InterruptedException ex) {
                    }
                }
            } else if(activity == Activity.CHASING) {
                try {
                    Thread.currentThread().sleep(CHASE_MODE_MILLISECONDS_PER_MOVE);
                } catch (InterruptedException ex) { }
            }

            synchronized (board) {
                if(!board.isSantaCaught()) {
                    // do after-sleep scanning
                    if (activity == Activity.SLEEPING) {
                        ArrayList<Position> neighbours = board.getNeighboursPositions(position, 1);
                        if (isSantaInTheCollection(neighbours)) {
                            activity = Activity.PLAYING;
                            board.setSantaCaught(true);
                            return;
                        } else {
                            try {
                                Position p = getGiftPosition(neighbours);
                                move(p);
                                activity = Activity.ENJOYING_THE_GIFT;
                                return;
                            } catch (Exception ex) { }
                        }
                    }

                    // pick next activity and move
                    if (activity == Activity.CHASING) {
                        if (System.currentTimeMillis() > activityEndTime) {
                            activity = Activity.SLEEPING;
                            setActivityTime();
                        } else {
                            doChaseMove();
                        }
                    } else {
                        findNextActivity();
                        if (activity == Activity.PLAYING) {
                            doRandomMove();
                        }
                        setActivityTime();
                    }

                    if(activity != Activity.SLEEPING && position.equals(santa.getPosition())) {
                        board.setSantaCaught(true);
                    }
                }
            }
        }
    }
}