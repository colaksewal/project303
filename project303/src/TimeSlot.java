import java.time.LocalDateTime;

public class TimeSlot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String roomId; // Optional: If you want to assign a room to a time slot

    // Constructor
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime, String roomId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomId = roomId;
    }

    // Getters and Setters
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    // toString method for easy printing and debugging
    @Override
    public String toString() {
        return "TimeSlot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}

