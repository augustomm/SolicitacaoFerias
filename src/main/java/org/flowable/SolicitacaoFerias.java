package org.flowable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

public class SolicitacaoFerias {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		//Configura o banco de dados H2
		ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
			      .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
			      .setJdbcUsername("sa")
			      .setJdbcPassword("")
			      .setJdbcDriver("org.h2.Driver")
			      .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		ProcessEngine processEngine = cfg.buildProcessEngine();

		//Faz o deploy do fluxo, utilizando o arquivo requisicao-ferias.bpmn.xml
		RepositoryService repositoryService = processEngine.getRepositoryService();
		Deployment deployment = repositoryService.createDeployment()
		  .addClasspathResource("requisicao-ferias.bpmn20.xml")
		  .deploy();

		System.out.println("\n");
		System.out.println("EMPREGADO");
		System.out.println("-------------------------------");
		//Informações a serem coletadas do requisitante de férias, antes de iniciar o fluxo
		Scanner scanner= new Scanner(System.in);

		System.out.println("Digite sua matrícula (NNNN):");
		String matricula = scanner.nextLine();
		
		System.out.println("Qual o seu nome?");
		String empregado = scanner.nextLine();
		
		System.out.println("Qual o primeiro dia das férias (DD/MM/AAAA)? ");
		String inicioFerias = scanner.nextLine();

		System.out.println("Qual o último dia das férias (DD/MM/AAAA)? ");
		String fimFerias = scanner.nextLine();

		System.out.println("Qual a justificativa?");
		String justificativa = scanner.nextLine();
		
		//As informações coletadas são armazenadas num java.util.Map, que será passado para a incialização da process instance
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("matricula", matricula);
		variables.put("empregado", empregado);
		variables.put("inicioFerias", inicioFerias);
		variables.put("fimFerias", fimFerias);
		variables.put("justificativa", justificativa);
		
		//A instância do fluxo é iniciada
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("requisicaoFerias", variables);
		
		//O sistema exibe a lista de tarefas atribuídas ao usuário do grupo "gerentes"
		
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("gerentes").list();
		System.out.println("\n");
		System.out.println("GERENTE");
		System.out.println("-------------------------------");
		System.out.println("Você tem " + tasks.size() + " tarefa(s):");
		for (int i=0; i<tasks.size(); i++) {
		  System.out.println((i+1) + ") " + tasks.get(i).getName());
		}
		
		//O sistema solicita ao gerente a tarefa a ser executada. Em seguida, exibe a proposta de férias para aprovação ou rejeição
		System.out.println("\n");
		System.out.println("Digite o número da tarefa que você gostaria de executar:");
		int taskIndex = Integer.valueOf(scanner.nextLine());
		Task task = tasks.get(taskIndex - 1);
		Map<String, Object> processVariables = taskService.getVariables(task.getId());
		System.out.println(processVariables.get("empregado") + " solicitou férias no período de: " +
		    processVariables.get("inicioFerias") + " a " + processVariables.get("fimFerias") + 
		    ". Você aprova (S/N)?");

		//O sistema armazena o resultado como uma variável de fluxo
		boolean aprovado = scanner.nextLine().toLowerCase().equals("s");
		variables = new HashMap<String, Object>();
		variables.put("aprovado", aprovado);
		taskService.complete(task.getId(), variables);

	}
}
