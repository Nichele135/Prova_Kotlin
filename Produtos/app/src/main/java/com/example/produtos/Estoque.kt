package com.example.produtos

class Estoque {
    private val produtos: MutableList<Produto> = mutableListOf()

    fun adicionarProduto(produto: Produto) {
        produtos.add(produto)
    }

    fun pegarProdutos(): List<Produto> {
        return produtos.toList()
    }

    fun calcularValorTotalEstoque(): Float {
        var total = 0f
        for (produto in produtos) {
            total += produto.precoP * produto.quantidadeP.toFloat()
        }
        return total
    }

    fun calcularQuantidadeTotalProdutos(): Int {
        var total = 0
        for (produto in produtos) {
            total += produto.quantidadeP
        }
        return total
    }

    companion object {
        val instance = Estoque()
    }
}