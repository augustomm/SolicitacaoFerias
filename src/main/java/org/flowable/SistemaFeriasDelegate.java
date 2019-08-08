package org.flowable;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class SistemaFeriasDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        System.out.println("Gravando informações no sistema de férias. Aguarde...");
        try {
			Thread.sleep(2000);
	        System.out.println("Férias agendadas!");
	        System.out.println("Matrícula: " + execution.getVariable("matricula"));
	        System.out.println("Nome: " + execution.getVariable("empregado"));
	        System.out.println("Início: " + execution.getVariable("inicioFerias"));
	        System.out.println("Fim: " + execution.getVariable("inicioFerias"));
	        System.out.println("Justificativa: " + execution.getVariable("justificativa"));

        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
