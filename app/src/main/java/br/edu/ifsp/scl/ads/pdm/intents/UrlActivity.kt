package br.edu.ifsp.scl.ads.pdm.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import br.edu.ifsp.scl.ads.pdm.intents.Constante.URL
import br.edu.ifsp.scl.ads.pdm.intents.databinding.ActivityUrlBinding

class UrlActivity : AppCompatActivity() {
    // tem que ser a mesma classe em que estamos
    private lateinit var aub: ActivityUrlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aub = ActivityUrlBinding.inflate(layoutInflater)
        setContentView(aub.root)
        supportActionBar?.subtitle = "UrlActivity"

        // recebendo um dado(String) da primeira tela
        // usa a mesma chave que mandou para receber
        val urlAnterior = intent.getStringExtra(URL) ?: ""
//        if (urlAnterior.isNotEmpty()) {
//            aub.urlEt.setText(urlAnterior)
//        }

        // faz a mesma coisa que o código anterior
        urlAnterior.takeIf { it.isNotEmpty() }.also {
            aub.urlEt.setText(it)
        }

        aub.entrarUrlBt.setOnClickListener(object: OnClickListener {
            override fun onClick(p0: View?) {
                val retornoIntent = Intent()
                // coloca o valor digitado na constante URL para enviar para outra tela
                retornoIntent.putExtra(URL, aub.urlEt.text.toString())
                setResult(RESULT_OK, retornoIntent)
                // onPause, onStop, onDestroy e volta pra tela anterior
                finish()
            }
        })
    }
}