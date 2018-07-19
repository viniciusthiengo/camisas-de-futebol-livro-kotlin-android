package thiengo.com.br.camisasdefutebol.extension

import java.util.*

/*
 * O algoritmo de apresentação de preço em formato
 * brasileiro foi colocado em um método de extensão,
 * pois este algoritmo será utilizado em mais de
 * um trecho de todo o projeto, assim evitamos a
 * repetição de código.
 * */
fun Float.priceBRFormat( moneySign: String ) =
    /*
     * A vírgula em "%s %,.2f" juntamente a Locale.GERMANY
     * permite que tenhamos a separação de milhares no
     * valor informado, por exemplo: o valor 1459 será
     * apresentado como 1.459,00.
     * */
    String.format(
        Locale.GERMANY,
        "%s %,.2f",
        moneySign,
        this
    )