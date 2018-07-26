package thiengo.com.br.camisasdefutebol

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.makeramen.roundedimageview.RoundedImageView
import thiengo.com.br.camisasdefutebol.domain.Shirt
import thiengo.com.br.camisasdefutebol.extension.priceBRFormat

class ShirtsAdapter(
        private val context: Context,
        private val shirts: List<Shirt>) :
        RecyclerView.Adapter<ShirtsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int) : ShirtsAdapter.ViewHolder {

        val v = LayoutInflater
            .from(context)
            .inflate(R.layout.shirt, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(shirts[position])
    }

    override fun getItemCount(): Int {
        return shirts.size
    }

    inner class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivShirt: RoundedImageView
        val tvSoccerTeam: TextView
        val tvLevelSizeBrand: TextView
        val tvDelivery: TextView
        val tvAmount: TextView
        val tvPrice: TextView

        init {
            ivShirt = itemView.findViewById(R.id.iv_shirt)
            tvSoccerTeam = itemView.findViewById(R.id.tv_soccer_team)
            tvLevelSizeBrand = itemView.findViewById(R.id.tv_level_size_brand)
            tvDelivery = itemView.findViewById(R.id.tv_frete)
            tvAmount = itemView.findViewById(R.id.tv_amount)
            tvPrice = itemView.findViewById(R.id.tv_price)
        }

        fun setData(shirt: Shirt) {
            ivShirt.setImageResource(shirt.imageId)

            /*
             * Note que em todo o projeto trabalhamos com
             * referências de Strings, como em R.string.uniform.
             * Isso facilita a posterior internacionalização
             * de aplicativo caso seja um passo importante no
             * momento de expansão de público alvo dele. Acostume-se
             * a fazer isso em qualquer um dos projetos em que
             * você trabalhar, pois até mesmo os códigos
             * repetidos, Strings utilizadas em mais de um ponto,
             * são diminuídos e o ponto de atualização passa a
             * ser somente em arquivos XML de definição de Strings.
             * */

            tvSoccerTeam.text = shirt.soccerTeam
            tvLevelSizeBrand.text = shirt.levelSizeBrandHumanFormat(
                context.getString(R.string.uniform)
            )
            tvDelivery.text = shirt.deliveryHumanFormat(
                context.getString(R.string.freight)
            )
            tvAmount.text = shirt.amountHumanFormat(
                context.getString(R.string.unity),
                context.getString(R.string.units)
            )
            tvPrice.text = shirt.price.priceBRFormat(
                context.getString(R.string.money_sign)
            )
        }
    }
}