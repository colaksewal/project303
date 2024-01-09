//Compose classroom class
public class Classroom {

    private String roomId;
    private int capacity;

    //constructor of class
    public Classroom(String roomId, int capacity) {
        this.roomId = roomId;
        this.capacity = capacity;
    }

    //Getters
    public String getRoomId() {
        return roomId;
    }

    public int getCapacity() {
        return capacity;
    }

    //Setter
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "RoomID: " + roomId + ", Capacity: " + capacity;
    }
}
