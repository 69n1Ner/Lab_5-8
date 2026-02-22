import java.util.ArrayList;
import java.util.List;

public class InputManager {
    Invoker invoker;
    private String command;
    private List<String> arguments;

    public InputManager(Invoker invoker) {
        this.arguments = new ArrayList<>();
        this.invoker = invoker;
    }
    public void separate(String input) {
        if (input == null || input.isEmpty()){
            throw new InvalidInput("Пустая команда");
        }

        int start = 0;
        int end;
        boolean wordFlag = false;
        boolean catchFlag = false;
        List<String> wordList = new ArrayList<>();
        String word;
        for (int i = 0; i < input.length(); i++) {
            input = input.trim().toLowerCase();
            if (input.charAt(i) == ' ' && start<i) {
                start = i;
            } else {
                end = i + 1;
                try {
                    if (input.charAt(end) == ' ') {
                        wordFlag = true;
                    }
                } catch (IndexOutOfBoundsException e) {
                    catchFlag = true;
                } finally {
                    if (wordFlag || catchFlag) {
                        word = input.substring(start, end);
                        if (!word.isEmpty())
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

    public boolean isValid(String input){
        String specialSymbols = "!@#$%^&*()+\"';:./?,`~№\\=<>[]{}йцукенгшщзхъфывапролджэячсмитьбю";
        for (int i =0; i<input.length();i++){
            if (specialSymbols.indexOf(input.charAt(i)) != -1){
                throw new InvalidInput("Строка содержит недопустимый символ: "+ input.charAt(i));
            }
        }

        if (!invoker.contains(input)){
            String commandList = "";
            for (String command: invoker.allCommands()){
                commandList += command + "\n";
            }

           throw new InvalidInput("Неверная команда: "+ input + "\n"+ "Доступные команды:" + "\n" + commandList);
        }

        if (input.length()>255){
            throw new InvalidInput("Слишком длинная строка! Максимальная длина 255");
        }


        return true;
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
