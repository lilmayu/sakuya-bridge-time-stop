package dev.mayuna.sakuyabridge.objects;

import java.util.Objects;

public class TestConfigObject {

    public int someNumber = 69;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof TestConfigObject that)) {
            return false;
        }
        return someNumber == that.someNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(someNumber);
    }
}
