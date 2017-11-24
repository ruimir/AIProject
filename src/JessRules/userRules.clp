;Definição de mensagens
(deftemplate ACLMessage (slot communicative-act) (slot sender) (multislot receiver)
              (slot reply-with) (slot in-reply-to) (slot envelope)
              (slot conversation-id) (slot protocol)
              (slot language) (slot ontology) (slot content)
              (slot encoding) (multislot reply-to) (slot reply-by))

;recebe proposta de estação, aceitar, recusae, ou recusar novas propostas as well?
;ideia:ter um limite de ofertas, quando ultrapassar, recusar novas propostas
;questão, e se user sair da area!?

;para uma estação, quantas tentativas fez?
(bind ?tries (new java.util.HashMap)) <Java-Object:java.util.HashMap>




(defrule good-offer
 "Aceitar proposta caso o desconto oferecido seja muito bom, neste caso, mais de 70%"
 ?m <- (ACLMessage (communicative-act PROPOSAL) (sender ?s) (content ?c) (receiver ?r) {content > 0.7})
 (MyAgent (name ?n))
 =>
 (assert (ACLMessage (communicative-act ACCEPT-PROPOSAL) (sender ?n) (receiver ?s) ))
 (retract ?m)
)


(defrule proposal-between
 "Aceitar proposta caso o deconto seja pequeno"
 ?m <- (ACLMessage (communicative-act PROPOSAL) (sender ?s) (content ?c) (receiver ?r) {content > 0.4 && content < 0.7})
 (MyAgent (name ?n))
 =>
  (bind ?odd (call Math random))
  (if (> ?odd 0.5) then
   (assert (ACLMessage (communicative-act ACCEPT-PROPOSAL) (sender ?n) (receiver ?s) ))

  else
  ;se tentativas pelo agente <3
  (if (call ?tries containsKey ?s)
    then
    (if (> (call get ?tries ?s) 3) then
    (assert (ACLMessage (communicative-act REJECT-PROPOSAL) (sender ?n) (receiver ?s) (content true)))
     else
    (call ?tries put ?s (+ 1 (call ?tries get ?s)))
    (assert (ACLMessage (communicative-act REJECT-PROPOSAL) (sender ?n) (receiver ?s) (content false))))
  else (call ?tries put ?s 1) (assert (ACLMessage (communicative-act REJECT-PROPOSAL) (sender ?n) (receiver ?s) (content false))) )
  )

 (retract ?m)
)





;TODO:TESTAR -> e ver se o content reconhece!
(defrule proposal-refusal
 "Aceitar proposta caso o deconto seja pequeno"
 ?m <- (ACLMessage (communicative-act PROPOSAL) (sender ?s) (content ?c) (receiver ?r) {content < 0.4})
 (MyAgent (name ?n))
 =>
  ;se tentativas pelo agente <3
  (if (call ?tries containsKey ?s)
    then
    (if (> (call get ?tries ?s) 3) then
    (assert (ACLMessage (communicative-act REJECT-PROPOSAL) (sender ?n) (receiver ?s) (content true)))
     else
    (call ?tries put ?s (+ 1 (call ?tries get ?s)))
    (assert (ACLMessage (communicative-act REJECT-PROPOSAL) (sender ?n) (receiver ?s) (content false))))
  else (call ?tries put ?s 1) (assert (ACLMessage (communicative-act REJECT-PROPOSAL) (sender ?n) (receiver ?s) (content false))) )

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