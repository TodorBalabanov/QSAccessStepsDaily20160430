#install.packages("igraph")
#install.packages("network") 
#install.packages("sna")
#install.packages("ndtv")

# Load data.
nodes <- read.csv("nodes.csv", header=T, as.is=T)
links <- read.csv("edges.csv", header=T, as.is=T)

# Check data.
head(nodes)
head(links)
nrow(nodes); length(unique(nodes$id))
nrow(links); nrow(unique(links[,c("from", "to")]))

library(igraph)

net <- graph.data.frame(links, nodes, directed=T)
net

E(net)       # The edges of the "net" object
V(net)       # The vertices of the "net" object
E(net)$weight  # Edge attribute "weight"
V(net)$type.label # Vertex attribute "type"

# Removing the loops in the graph.
#net <- simplify(net, remove.multiple = F, remove.loops = T) 

# Generate colors base on neuron type:
colrs <- c("cyan", "blue", "green", "red")
V(net)$color <- colrs[V(net)$type.number]

# The labels are currently node IDs.
# Setting them to NA will render no labels:
V(net)$label <- NA

#change arrow size and edge color:
E(net)$arrow.size <- 0.01
E(net)$edge.color <- "black"

plot(net)
#plot(net,layout=layout_in_circle)
legend(x=-1.5, y=-0.5, c("Bias","Input", "Regular", "Output"), pch=21,
       col="#777777", pt.bg=colrs, pt.cex=1.2, cex=1.0, bty="n", ncol=1)