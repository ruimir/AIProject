;Definição de mensagens
(deftemplate ACLMessage (slot communicative-act) (slot sender) (multislot receiver)
              (slot reply-with) (slot in-reply-to) (slot envelope)
              (slot conversation-id) (slot protocol)
              (slot language) (slot ontology) (slot content)
              (slot encoding) (multislot reply-to) (slot reply-by))


;Quando recebe mensagem de user devolver bicicleta -> mandar mensagem para controlador



; Envio de propostas
(defrule proposal
 ?m <- (ACLMessage (communicative-act INFORM) (sender ?s) (content ?c) (receiver ?r) {TODO: content ser de devovulção})
 (MyAgent (name ?n))
 =>
 (assert (ACLMessage (communicative-act ACCEPT-PROPOSAL) (sender ?n) (receiver ?s) (content cooling) ))
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