package GuevaraWilmer.cazarpatos

data class Player(
    var username: String,
    var huntedDucks: Int
) {
    // Constructor sin parámetros requerido para Firebase u otros usos
    constructor() : this("", 0)
}
