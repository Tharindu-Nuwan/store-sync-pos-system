package lk.ijse.dep11.app.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable {
    private String id;
    private String name;
    private String address;

    @Override
    public String toString() {
        return id;
    }
}
