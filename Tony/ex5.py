name = 'Anthony O. Odufuye'
age = 31 # For realsies
height = 74 # inches
weight = 225 # pounds
eyes = 'Brown'
teeth = 'White'
hair = 'Black'

print ("Let's talk about %s.") % name
print ("He's %r inches tall.") % height
print ("He's %d pounds heavy.") % weight
print ("Actually that's not too heavy.")
print ("He's got %r eyes and %s hair.") % (eyes, hair)
print ("His teeth are usually %s depending on the tea.") % teeth

# This lineis tricky, try to get it exactly right
print ("If I add %d, %d, and %d, I get %d.") % (age, height, weight, age + height + weight)


num = raw_input("Enter a number to convert to KG from pounds: ")

