(function () {
    const cep = document.getElementById('cep');
    if (!cep) return;

    const mensagem = document.getElementById('cep-mensagem');
    const somenteDigitos = valor => valor.replace(/\D/g, '');

    async function buscarCep() {
        const valor = somenteDigitos(cep.value);
        if (valor.length !== 8) return;
        mensagem.textContent = 'Consultando CEP...';
        try {
            const resposta = await fetch('https://viacep.com.br/ws/' + valor + '/json/');
            if (!resposta.ok) throw new Error('Falha na consulta');
            const dados = await resposta.json();
            if (dados.erro) throw new Error('CEP não encontrado');
            document.getElementById('logradouro').value = dados.logradouro || '';
            document.getElementById('bairro').value = dados.bairro || '';
            document.getElementById('cidade').value = dados.localidade || '';
            document.getElementById('uf').value = dados.uf || '';
            mensagem.textContent = 'Endereço preenchido automaticamente.';
            document.getElementById('numero').focus();
        } catch (erro) {
            mensagem.textContent = 'Não foi possível localizar esse CEP. Preencha os dados manualmente.';
        }
    }

    cep.addEventListener('blur', buscarCep);
    cep.addEventListener('input', () => {
        const valor = somenteDigitos(cep.value);
        cep.value = valor.length > 5 ? valor.slice(0, 5) + '-' + valor.slice(5, 8) : valor;
    });
})();
