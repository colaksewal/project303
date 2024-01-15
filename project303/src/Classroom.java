/**
 * The {@code Classroom} class represents a room in an educational setting.
 * It includes information about the room's ID and capacity.
 */
public class Classroom {

    // Fields

    /**
     * The unique identifier for the classroom.
     */
    private String roomId;

    /**
     * The maximum number of individuals that the classroom can accommodate.
     */
    private int capacity;

    // Constructor

    /**
     * Constructs a new {@code Classroom} with the specified room ID and capacity.
     *
     * @param roomId    The unique identifier for the classroom.
     * @param capacity  The maximum number of individuals the classroom can accommodate.
     */
    public Classroom(String roomId, int capacity) {
        this.roomId = roomId;
        this.capacity = capacity;
    }

    // Getters

    /**
     * Retrieves the room ID of the classroom.
     *
     * @return The unique identifier for the classroom.
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Retrieves the maximum capacity of the classroom.
     *
     * @return The maximum number of individuals the classroom can accommodate.
     */
    public int getCapacity() {
        return capacity;
    }

    // Setter

    /**
     * Sets the maximum capacity of the classroom.
     *
     * @param capacity The new maximum number of individuals the classroom can accommodate.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // Override Methods

    /**
     * Returns a string representation of the {@code Classroom} object.
     *
     * @return A string containing the room ID and capacity of the classroom.
     */
    @Override
    public String toString() {
        return "RoomID: " + roomId + ", Capacity: " + capacity;
    }
}
