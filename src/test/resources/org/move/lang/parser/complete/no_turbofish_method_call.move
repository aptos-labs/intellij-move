module 0x1::no_turbofish_method_call {
    fun main() {
        c.receiver<u8>();
        c.receiver < u8>();
    }
}
