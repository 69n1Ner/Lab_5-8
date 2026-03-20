package IO;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    // Формат даты: измените под ваши нужды
    // "yyyy-MM-dd" — ISO стандарт (2020-02-01)
    // "dd-MM-yyyy" — ваш формат (01-02-2020)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(v, FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Неверный формат даты: " + v + ". Ожидается дд-ММ-гггг");
            throw e;
        }
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.format(FORMATTER);
    }
}