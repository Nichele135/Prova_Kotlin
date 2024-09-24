package com.example.produtos

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
             AppNavigation()
        }
    }
}



@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var listaProduto by remember { mutableStateOf(listOf<Produto>()) }


    NavHost(navController = navController, startDestination = "cadastroProduto") {
        composable("cadastroProduto") { cadastroProduto(navController, listaProduto) { listaProduto = it } }
        composable("listarProdutos") { listarProdutos(navController, listaProduto) }
        composable("ProdutoItem") { listarProdutos(navController, listaProduto) }
        composable("detalhes/{produtoJson}") { backStackEntry ->
            val produtoJson = backStackEntry.arguments?.getString("produtoJson")
            val produto = Gson().fromJson(produtoJson, Produto::class.java)
            DetalheProduto(produto, navController)
        }
        composable("estatisticas") {
            estatiscas(navController)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cadastroProduto(navController: NavHostController, listaProduto: List<Produto>, updateLista: (List<Produto>) -> Unit) {

    var nomeP by remember {
        mutableStateOf("")
    }

    var categoriaP by remember {
        mutableStateOf("")
    }

    var precoP by remember {
        mutableStateOf("")
    }

    var quantidadeP by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Cadastrar produto")


        TextField(
            value = nomeP,
            onValueChange = { nomeP = it },
            label = { Text(text = "nome do produto") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = categoriaP,
            onValueChange = { categoriaP = it },
            label = { Text(text = "categoria do produto") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(15.dp))


        TextField(
            value = precoP,
            onValueChange = { precoP = it },
            label = { Text(text = "preço do produto") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(15.dp))


        TextField(
            value = quantidadeP,
            onValueChange = { quantidadeP = it },
            label = { Text(text = "quantidade do produto") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(15.dp))



        Button(onClick = {
            // Lógica para cadastrar o produto
            if (nomeP.isNotEmpty() && categoriaP.isNotEmpty() &&
                precoP.isNotEmpty() && quantidadeP.all { it.isDigit() } &&
                quantidadeP.isNotEmpty() && quantidadeP.all { it.isDigit() }) {

                val precoFloat = precoP.toFloat()
                val quantInt = quantidadeP.toInt()

                if (precoFloat <= 0) {
                    Toast.makeText(context, "Preço não pode ser menor que 0", Toast.LENGTH_SHORT).show()
                } else if (quantInt < 1) {
                    Toast.makeText(context, "Quantidade deve ser pelo menos 1", Toast.LENGTH_SHORT).show()
                } else {
                    val produto = Produto(nomeP, categoriaP, precoFloat, quantInt)
                    Estoque.instance.adicionarProduto(produto)
                    navController.navigate("listarProdutos")
                }
            } else {
                Toast.makeText(context, "Todos os campos são obrigatórios e preço/quantidade devem ser numéricos", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Cadastrar")
        }

        Button(onClick = { navController.navigate("listarProdutos") }) {
            Text("Listar Produtos")
        }
    }



}

@Composable
fun listarProdutos(navController: NavHostController, listaProduto: List<Produto>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Lista de Produtos")

        LazyColumn {
            items(Estoque.instance.pegarProdutos()) { produto ->

                Text(text = "${produto.nome} (${produto.quantidadeP} unidades)")
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    val produtoJson = Gson().toJson(produto)
                    navController.navigate("detalhes/$produtoJson")
                }) {
                    Text("Detalhes")
                }
                Button(onClick = { navController.popBackStack() }) {
                    Text("Voltar")
                }
            }
        }
        Button(onClick = { navController.navigate("Estatisticas") }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver Estatísticas")
        }
    }
}

@Composable
fun ProdutoItem(produto: Produto, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Nome: ${produto.nome} (${produto.quantidadeP})")
        }

        Button(onClick = {
            navController.navigate("DetalheProduto/${produto.nome}/${produto.categoriaP}/${produto.precoP}/${produto.quantidadeP}")
        }) {
            Text("Detalhes")
        }
    }
    Button(onClick = { navController.navigate("cadastroProduto") }, modifier = Modifier.fillMaxWidth()) {
        Text("Cadastrar Novo Produto")
    }
}

@Composable
fun DetalheProduto(produto: Produto, navController: NavHostController){
    Column(modifier = Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.Center) {
        Text(text = "DETALHES DO PRODUTO", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = "Nome: ${produto.nome}")
        Text(text = "Categoria: ${produto.categoriaP}")
        Text(text = "Preço: ${produto.precoP}")
        Text(text = "Quantidade: ${produto.quantidadeP}")

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}

@Composable
fun estatiscas(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Estatísticas do Estoque"
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Valor Total do Estoque: R$ ${Estoque.instance.calcularValorTotalEstoque()}")
        Text(text = "Quantidade Total de Produtos: ${Estoque.instance.calcularQuantidadeTotalProdutos()}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Voltar")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLayout() {
    AppNavigation()
}
