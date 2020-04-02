package th.ac.kku.cis.mobileapp.myaccount

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

public class Adapter(val mCtx: Context,
                            var resource:Int,
                            var items:List<receipts>)// เก็บ class รายรับรายจ่ายเป็นลิสไว้ใน items
    : ArrayAdapter<receipts>(mCtx,resource,items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //return super.getView(position, convertView, parent)
        val layout: LayoutInflater = LayoutInflater.from(mCtx)

        val v: View = layout.inflate(resource,null)
        val imageViews : ImageView = v.findViewById(R.id.imageView3)
        val textView : TextView = v.findViewById(R.id.textView)
        val textView2 : TextView = v.findViewById(R.id.textView2)
        val textView4 : TextView = v.findViewById(R.id.textView4)
        val textView5 : TextView = v.findViewById(R.id.textView5)
        val textView6 : TextView = v.findViewById(R.id.textView6)

        val receipt: receipts = items[position]


        textView2.text = receipt.Amount//นำ แอทริบิ้วของclass รายรับรายจ่ายมาเก็บไว้ใน id ของโมเดลที่เราสร้างขึ้น
        textView4.text = receipt.category
        textView5.text = receipt.detail
        textView6.text = receipt.from
        textView.text = receipt.date
        Picasso.get().load(receipt.image_uri).into(imageViews)
        return v
    }
}