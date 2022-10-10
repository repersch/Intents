package br.edu.ifsp.scl.ads.pdm.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.ifsp.scl.ads.pdm.intents.Constante.URL
import br.edu.ifsp.scl.ads.pdm.intents.databinding.ActivityMainBinding
import java.net.URI

class MainActivity : AppCompatActivity() {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var urlArl: ActivityResultLauncher<Intent>
    private lateinit var permissaoChamadaArl: ActivityResultLauncher<String>
    private lateinit var pegarImagemArl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        // informa embaixo do nome do app em qual tela esta
        supportActionBar?.subtitle = "MainActivity"

        // se a activity fechou com result ok, seta a string no textView
        urlArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado: ActivityResult ->
            if (resultado.resultCode == RESULT_OK) {
                val urlRetornada = resultado.data?.getStringExtra(URL) ?: ""
                amb.urlTv.text = urlRetornada
            }
        }

        permissaoChamadaArl = registerForActivityResult(
            // abre uma tela para solicitar permissões
            ActivityResultContracts.RequestPermission()
        )
        // control + o mostra as implementações possíveis
        // quando a tela de permissao fechar, se voltar true = permissão concedida
        { concedida ->
            if (concedida!!) {
                chamarNumero(chamar = true)
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Permissão necessária para execução",
                    Toast.LENGTH_LONG
                ).show()
                // como só tem uma tela ativa, fecha o app
                finish()
            }
        }


        pegarImagemArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { resultado: ActivityResult ->
            // aqui o ok significa que o usuário fechou a galeria pq escolheu uma imagem
            // recendo o path da imagem
            if (resultado.resultCode == RESULT_OK) {
                // o segundo data é o path completo da imagem
                val imagemUri = resultado.data?.data
                imagemUri?.let {
                    amb.urlTv.text = it.toString()
                }
                // abrindo o visualizador de imagem
                val visualizarImagemIntent = Intent(ACTION_VIEW, imagemUri)
                startActivity(visualizarImagemIntent)
            }
        }


        amb.entrarUrlBt.setOnClickListener {
            val urlActivityIntent = Intent(this, UrlActivity::class.java)
//            val urlActivityIntent = Intent("SEGUNDA_TELA_DO_PROJETO_INTENT")
            // primeiro parâmetro esta definido em constante
            // enviando um texto para a segunda tela
            urlActivityIntent.putExtra(URL, amb.urlTv.text)
            // abrindo outra tela e tratando o retorno da tela anterior
            urlArl.launch(urlActivityIntent)
        }
    }


    // coloca o menu na action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // trata das escolhas das opções de menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // no when retorn o que esta na ultima linha
        return when (item.itemId) {
            R.id.viewMi -> {
                // abrir o navegador na url digitada pelo usuario
                val url = Uri.parse(amb.urlTv.text.toString())
                val navegadorIntent = Intent(ACTION_VIEW, url)
                startActivity(navegadorIntent)
                true
            }

            R.id.dialMi -> {
                chamarNumero(false)
                true
            }

            R.id.callMi -> {
                // verificar a versão do android (nesse caso se é maior ou igual o marshmellow)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        // fazer a chamada
                        chamarNumero(true)

                    // se superior ou igual a M
                        // verificar se tem permissão e solicitar se necessario
                    // caso contrário
                        // fazer chamada
                    } else {
                        // solicitar permissão
                        permissaoChamadaArl.launch(CALL_PHONE)
                    }
                }


                true
            }

            R.id.pickMi -> {

                val pegarImagemIntent = Intent(ACTION_PICK)
                // pega o path do diretório público de imagens do device
                val diretorioImagens = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .path

                pegarImagemIntent.setDataAndType(Uri.parse(diretorioImagens), "image/*")
                // procura algum aplicativo instalado que possua a action_pick e o tipo de dado definido (image/*)
                pegarImagemArl.launch(pegarImagemIntent)

                true
            }

            R.id.chooserMi -> {

                val escolherAppIntent = Intent(ACTION_CHOOSER)
                // o tipo de app que eu quero que abra na lista
                val informacoesIntent = Intent(ACTION_VIEW, Uri.parse(amb.urlTv.text.toString()))
                // personaliza a caixa de opções
                escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha seu navegador")
                // vincula uma intent a outra
                escolherAppIntent.putExtra(EXTRA_INTENT, informacoesIntent)
                startActivity(escolherAppIntent)
                true
            }

            else -> { false }
        }
    }

    private fun chamarNumero(chamar: Boolean) {
        val uri = Uri.parse("tel: ${amb.urlTv.text.toString()}")
        val intent = Intent(if (chamar) ACTION_CALL else ACTION_DIAL)
        intent.data = uri
        startActivity(intent)
    }
}