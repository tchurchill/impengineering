# Two scenarios where the script doesn't work as planned:
#	1. When a blank sheet is encountered the script barfs (this I should be able to fix)
#	2. When a sheet is a list of Qualifiers only, it must have the value "Qualifier Sheet" in 1A

require 'rubygems'
require 'roo'
require 'csv'

valueHash = Hash.new
d = Dir.new('./')
d.each do |item|
	if item.include? ".xlsx" and not item.include? "~$"
		cur = Roo::Excelx.new(item)
		cur.sheets.each do |sheet|
			cur.default_sheet = sheet
			if cur.cell(1,'A').to_s.downcase.include? "qualifier"
				existingValues = Array(valueHash[sheet])
				2.upto(cur.last_row) do |row|
					value = cur.cell(row,'A').to_s + "- " + cur.cell(row,'B').to_s.strip
					if not existingValues.include? value
						existingValues.push(value)
					end
				end
				valueHash[sheet] = existingValues
			else
				cur.first_row.upto(cur.last_row) do |row|
					if cur.cell(row, 'C') == "ID"
						key = cur.cell(row,'B')
						value = cur.cell(row,'J').to_s.gsub(/\(.[^\)]*\)|"|[^a-zA-Z0-9 \-\n]/,'')
						values = value.split("\n")
						existingValues = Array(valueHash[key])
						values.each do |val|
							next if val.downcase.include? "see"
							val = val.strip
							if not existingValues.include? val
								existingValues.push(val)
							end
						end
						valueHash[key] = existingValues
					end
				end
			end
		end
	end
end

#Ugly print for easier debugging
#CSV.open("./qual_list.csv", "wb") {|csv| valueHash.to_a.each {|elem| csv << elem} }

#Pretty(ish) for BA's
CSV.open("./qual_list.csv", "wb") do |csv|
	valueHash.each do |key,value|
		value = value.sort 
		value.each_index {|i| value[i] = value[i].to_s[1..-1] if value[i].to_s.start_with? "-" }
		csv << [key, value.join("\n")]
	end
end
