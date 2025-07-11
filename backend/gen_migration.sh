read -p "Enter description: " desc

datetime=$(date +"%Y%m%d%H%M%S")

desc_clean=$(echo "$desc" | tr ' ' '_' | tr -d '"')

filename="V${datetime}__${desc_clean}.sql"

target_dir="src/main/resources/db/migration"

mkdir -p "$target_dir"
echo "-- Migration file created at $datetime" > "$target_dir/$filename"

echo "Created: $filename"
