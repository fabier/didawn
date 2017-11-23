package didawn.gson

class BaseJSON {

    // for << operator
    def leftShift(Object o) {
        o.properties.findAll { it.value && "class".equals(it.key) }.each {
            setProperty((String) it.key, it.value)
        }
    }
}
