;Definição de mensagens
(deftemplate ACLMessage (slot communicative-act) (slot sender) (multislot receiver)
              (slot reply-with) (slot in-reply-to) (slot envelope)
              (slot conversation-id) (slot protocol)
              (slot language) (slot ontology) (slot content)
              (slot encoding) (multislot reply-to) (slot reply-by))

;recebe proposta de estação, aceitar, recusae, ou recusar novas propostas as well?
;ideia:ter um limite de ofertas, quando ultrapassar, recusar novas propostas
;questão, e se user sair da area!?


(defrule good-offer
 ?m <- (ACLMessage (communicative-act PROPOSAL) (sender ?s) (content ?c) (receiver ?r) {content>0.7 ;70%desconto!})
 (MyAgent (name ?n))
 =>
 (assert (ACLMessage (communicative-act ACCEPT-PROPOSAL) (sender ?n) (receiver ?s) (content cooling) ))
 (retract ?m)
)

(defrule proposal-refusal
 ?m <- (ACLMessage (communicative-act PROPOSAL) (sender ?s) (content ?c) (receiver ?r) {content<0.4 ;apenas 40%})
 (MyAgent (name ?n))
 =>
 ;se tentativas pelo agente <3
 (assert (ACLMessage (communicative-act REJECT-PROPOSAL) (sender ?n) (receiver ?s) (content cooling) ))
 ;senão
 assert um para recusar mais ofertas
 (retract ?m)
)



(defrule send-a-message
    (MyAgent (name ?n))
    ?m <-(ACLMessage(sender ?n) (receiver ?r) (content ?c) (communicative-act ?ca))
    =>
    (printout t "Time to send a message!" crlf)
     ;(printout t "Sender " ?n crlf)
      ;(printout t "Receiver " ?r crlf)
       ;(printout t "Content " ?c crlf)
        ;(printout t "Performative " ?ca crlf)

    (send ?m) (retract ?m) )



(watch facts)
;(watch all)


(reset)