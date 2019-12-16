package IRC;

public class Channel {
    private String name;
    private String topic;

    public Channel(String name, String topic) {
        this.name = name;
        this.topic = topic;
    }


    public String getName() {
        return name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
