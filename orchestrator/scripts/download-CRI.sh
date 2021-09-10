FILE=./v0.22.1.tar.gz
if [ -f "$FILE" ]; then
  echo "$FILE already downloaded."
else
  echo "Downloading file..."
  wget https://github.com/kubernetes/cri-api/archive/refs/tags/v0.22.1.tar.gz
fi

DIR=./v0.22.1
if [ -d "$DIR" ]; then
  if [ -z "$(ls -A /v0.22.1)" ]; then
    rmdir "$DIR"
    echo "Unpacking file..."
    tar -xzf "$FILE"
  else
    echo "Directory already exists"
  fi
else
  echo "Unpacking file..."
  tar -xzf "$FILE"
fi