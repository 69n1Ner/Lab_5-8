import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputManager {
    private String command;
    private List<String> arguments;

    public InputManager() {
        this.arguments = new ArrayList<>();
    }
    //todo сделать сепарацию
    public void separate(String input) {
        int start = 0;
        int end = 1;
        boolean wordFlag = false;
        boolean catchFlag = false;
        List<String> wordList = new ArrayList<>();
        String word = "";
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == ' ') {
                start = i;
            } else {
                end = i + 1;
                word = input.substring(start + 1, end);
                try {
                    if (input.charAt(end) == ' ') {
                        wordFlag = true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    catchFlag = true;
                } finally {
                    if (wordFlag || catchFlag) {
                        wordList.add(word);
                        start = end;
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
