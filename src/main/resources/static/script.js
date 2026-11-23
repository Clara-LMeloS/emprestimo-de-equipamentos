const API = "";

const $ = (seletor) => document.querySelector(seletor);

let equipamentos = [];
let colaboradores = [];
let emprestimos = [];
let emprestimosAtrasados = [];

function mostrarMensagem(seletor, mensagem, tipo = "") {
    const elemento = $(seletor);
    elemento.textContent = mensagem;
    elemento.className = `mensagem ${tipo}`;
}

async function requisicao(url, opcoes = {}) {
    const resposta = await fetch(`${API}${url}`, {
        headers: { "Content-Type": "application/json", ...(opcoes.headers || {}) },
        ...opcoes
    });

    if (resposta.status === 204) return null;

    const texto = await resposta.text();
    let dados = null;
    if (texto) {
        try { dados = JSON.parse(texto); }
        catch { dados = texto; }
    }

    if (!resposta.ok) {
        const mensagem = dados?.message || dados?.mensagem || dados?.error || texto || `Erro HTTP ${resposta.status}`;
        throw new Error(mensagem);
    }
    return dados;
}

function formatarData(data) {
    if (!data) return "—";
    const [ano, mes, dia] = data.split("-");
    return `${dia}/${mes}/${ano}`;
}

function escapar(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, caractere => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#039;"
    })[caractere]);
}

function trocarTela(nome) {
    document.querySelectorAll(".tela").forEach(tela => tela.classList.toggle("ativa", tela.id === `tela-${nome}`));
    document.querySelectorAll(".item-menu").forEach(botao => botao.classList.toggle("ativo", botao.dataset.tela === nome));
    if (nome === "equipamentos") carregarEquipamentos();
    if (nome === "colaboradores") carregarColaboradores();
    if (nome === "emprestimos") carregarDadosEmprestimos();
}

document.querySelectorAll(".item-menu").forEach(botao => {
    botao.addEventListener("click", () => trocarTela(botao.dataset.tela));
});

async function carregarEquipamentos() {
    const corpo = $("#lista-equipamentos");
    try {
        equipamentos = await requisicao("/equipamentos") || [];
        corpo.innerHTML = equipamentos.length ? equipamentos.map(item => `
            <tr>
                <td>${escapar(item.nome)}</td>
                <td>${escapar(item.patrimonio)}</td>
                <td>${escapar(item.tipo)}</td>
                <td><span class="status ${item.disponivel ? "" : "indisponivel"}">${item.disponivel ? "Disponível" : "Emprestado"}</span></td>
            </tr>`).join("") : '<tr><td colspan="4">Nenhum equipamento cadastrado.</td></tr>';
        preencherSelectEquipamentos();
    } catch (erro) {
        corpo.innerHTML = `<tr><td colspan="4">${escapar(erro.message)}</td></tr>`;
    }
}

async function carregarColaboradores() {
    const corpo = $("#lista-colaboradores");
    try {
        colaboradores = await requisicao("/colaboradores") || [];
        corpo.innerHTML = colaboradores.length ? colaboradores.map(item => `
            <tr><td>${escapar(item.nome)}</td><td>${escapar(item.email)}</td><td>${escapar(item.id)}</td></tr>`).join("")
            : '<tr><td colspan="3">Nenhum colaborador cadastrado.</td></tr>';
        preencherSelectColaboradores();
    } catch (erro) {
        corpo.innerHTML = `<tr><td colspan="3">${escapar(erro.message)}</td></tr>`;
    }
}

async function carregarDadosEmprestimos() {
    const corpo = $("#lista-emprestimos");
    try {
        const [lista, atrasados] = await Promise.all([
            requisicao("/emprestimos"),
            requisicao("/emprestimos?atrasados=true")
        ]);
        emprestimos = lista || [];
        emprestimosAtrasados = atrasados || [];
        const atrasadosIds = new Set(emprestimosAtrasados.map(item => item.id));

        corpo.innerHTML = emprestimos.length ? emprestimos.map(item => {
            const ativo = item.dataDevolucao == null;
            const atrasado = atrasadosIds.has(item.id) || item.atrasado === true;
            const equipamento = equipamentos.find(e => e.id === item.equipamentoId);
            const colaborador = colaboradores.find(c => c.id === item.colaboradorId);
            const status = ativo ? (atrasado ? "Atrasado" : "Em andamento") : "Devolvido";
            return `<tr>
                <td>${escapar(item.id)}</td>
                <td>${escapar(equipamento?.nome || `ID ${item.equipamentoId}`)}</td>
                <td>${escapar(colaborador?.nome || `ID ${item.colaboradorId}`)}</td>
                <td>${formatarData(item.dataEmprestimo)}</td>
                <td>${formatarData(item.dataPrevistaDevolucao)}</td>
                <td><span class="status ${atrasado ? "atrasado" : ""}">${status}</span></td>
                <td>${ativo ? `<button class="botao botao-devolver" data-devolver="${escapar(item.id)}">Registrar devolução</button>` : "—"}</td>
            </tr>`;
        }).join("") : '<tr><td colspan="7">Nenhum empréstimo registrado.</td></tr>';
    } catch (erro) {
        corpo.innerHTML = `<tr><td colspan="7">${escapar(erro.message)}</td></tr>`;
    }
}

function preencherSelectEquipamentos() {
    const select = $("#select-equipamento");
    const disponiveis = equipamentos.filter(item => item.disponivel);
    select.innerHTML = '<option value="">Selecione um equipamento</option>' + disponiveis.map(item =>
        `<option value="${escapar(item.id)}">${escapar(item.nome)} — ${escapar(item.patrimonio)}</option>`).join("");
}

function preencherSelectColaboradores() {
    const select = $("#select-colaborador");
    select.innerHTML = '<option value="">Selecione um colaborador</option>' + colaboradores.map(item =>
        `<option value="${escapar(item.id)}">${escapar(item.nome)}</option>`).join("");
}

async function carregarPainel() {
    try {
        const [listaEquipamentos, listaEmprestimos, atrasados] = await Promise.all([
            requisicao("/equipamentos"),
            requisicao("/emprestimos"),
            requisicao("/emprestimos?atrasados=true")
        ]);
        const eq = listaEquipamentos || [];
        const emp = listaEmprestimos || [];
        $("#total-equipamentos").textContent = eq.length;
        $("#equipamentos-disponiveis").textContent = eq.filter(item => item.disponivel).length;
        $("#emprestimos-ativos").textContent = emp.filter(item => item.dataDevolucao == null).length;
        $("#emprestimos-atrasados").textContent = (atrasados || []).length;
        $("#aviso-inicio").textContent = "";
    } catch (erro) {
        $("#aviso-inicio").textContent = `Não foi possível carregar os indicadores: ${erro.message}`;
        $("#aviso-inicio").className = "aviso erro";
    }
}

$("#form-equipamento").addEventListener("submit", async evento => {
    evento.preventDefault();
    const formulario = evento.currentTarget;
    const dados = Object.fromEntries(new FormData(formulario).entries());
    try {
        await requisicao("/equipamentos", { method: "POST", body: JSON.stringify(dados) });
        formulario.reset();
        mostrarMensagem("#mensagem-equipamento", "Equipamento cadastrado com sucesso!", "sucesso");
        await carregarEquipamentos();
        await carregarPainel();
    } catch (erro) {
        mostrarMensagem("#mensagem-equipamento", erro.message, "erro");
    }
});

$("#form-colaborador").addEventListener("submit", async evento => {
    evento.preventDefault();
    const formulario = evento.currentTarget;
    const dados = Object.fromEntries(new FormData(formulario).entries());
    try {
        await requisicao("/colaboradores", { method: "POST", body: JSON.stringify(dados) });
        formulario.reset();
        mostrarMensagem("#mensagem-colaborador", "Colaborador cadastrado com sucesso!", "sucesso");
        await carregarColaboradores();
    } catch (erro) {
        mostrarMensagem("#mensagem-colaborador", erro.message, "erro");
    }
});

$("#form-emprestimo").addEventListener("submit", async evento => {
    evento.preventDefault();
    const dados = Object.fromEntries(new FormData(evento.currentTarget).entries());
    const url = `/emprestimos?equipamentoId=${encodeURIComponent(dados.equipamentoId)}&colaboradorId=${encodeURIComponent(dados.colaboradorId)}`;
    try {
        await requisicao(url, { method: "POST" });
        evento.currentTarget.reset();
        mostrarMensagem("#mensagem-emprestimo", "Empréstimo registrado com sucesso!", "sucesso");
        await Promise.all([carregarEquipamentos(), carregarColaboradores(), carregarDadosEmprestimos(), carregarPainel()]);
    } catch (erro) {
        mostrarMensagem("#mensagem-emprestimo", erro.message, "erro");
    }
});

$("#lista-emprestimos").addEventListener("click", async evento => {
    const botao = evento.target.closest("[data-devolver]");
    if (!botao) return;
    const id = botao.dataset.devolver;
    if (!confirm(`Confirma a devolução do empréstimo ${id}?`)) return;
    try {
        await requisicao(`/emprestimos/${id}/devolucao`, { method: "PUT" });
        await Promise.all([carregarEquipamentos(), carregarDadosEmprestimos(), carregarPainel()]);
    } catch (erro) {
        alert(`Não foi possível registrar a devolução: ${erro.message}`);
    }
});

$("#atualizar-painel").addEventListener("click", carregarPainel);
$("#recarregar-equipamentos").addEventListener("click", carregarEquipamentos);
$("#recarregar-colaboradores").addEventListener("click", carregarColaboradores);
$("#recarregar-emprestimos").addEventListener("click", carregarDadosEmprestimos);

async function iniciarAplicacao() {
    await Promise.all([carregarEquipamentos(), carregarColaboradores(), carregarPainel()]);
}
iniciarAplicacao();
