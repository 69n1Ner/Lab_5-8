import java.util.ArrayList;
import java.util.List;

public class InputManager {
    private String command;
    private List<String> arguments;

    public InputManager() {
        this.arguments = new ArrayList<>();
    }
    public void separate(String input) {
        int start = 0;
        int end = 1;
        boolean wordFlag = false;
        boolean catchFlag = false;
        List<String> wordList = new ArrayList<>();
        String word = "";
        for (int i = 0; i < input.length(); i++) {
            input = input.strip();
            if (input.charAt(i) == ' ' && start<i) {
                start = i;
            } else {
                end = i + 1;
                word = input.substring(start, end);
                try {
                    if (input.charAt(end) == ' ') {
                        wordFlag = true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    catchFlag = true;
                } finally {
                    if (wordFlag || catchFlag) {
                        wordList.add(word);
                        start = end+1;
                        wordFlag = false;
                        catchFlag = false;
                    }
                }
            }
        }

        this.command = wordList.get(0);
        if (wordList.size()>1){
            wordList.remove(0);
            this.arguments = new ArrayList<>(wordList);
        }
    }

    public void clear() {
        this.command = null;
        this.arguments.clear();
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getCommand() {
        return command;
    }
}
