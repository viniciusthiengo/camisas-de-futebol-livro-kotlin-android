package thiengo.com.br.camisasdefutebol.util

import java.util.*

/*
 * Implementação simples do algoritmo de Luhn, algoritmo
 * utilizado para validação de números de cartão de
 * crédito. Mais sobre o algoritmo de Luhn nos links a
 * seguir:
 *
 * https://en.wikipedia.org/wiki/Luhn_algorithm
 * https://gist.github.com/mdp/9691528
 * */
fun luhnAlgorithm( creditCardNumber: String ): Boolean{
    var sum = 0
    var alternate = false

    for (i in creditCardNumber.length - 1 downTo 0) {

        var n = Integer.parseInt( creditCardNumber.substring(i, i + 1) )

        if (alternate) {
            n *= 2
            if (n > 9) {
                n = n % 10 + 1
            }
        }
        sum += n
        alternate = !alternate
    }

    return sum % 10 == 0
}

/*
 * Simulador de código de compra realizada. O "+ 10000000"
 * é necessário, pois Random().nextInt(10000000) pode gerar
 * qualquer valor entre 0 e 9999999. Com o "+ 10000000"
 * garantimos os 8 digitos de código de compra.
 * */
fun orderCodeGenerator(): String{
    val code = Random().nextInt(10000000) + 10000000
    return String.format(" %d", code)
}